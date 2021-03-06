package com.mumulcom.mumulcom.src.user.service;

import com.github.dozermapper.core.Mapper;
import com.mumulcom.mumulcom.config.BaseException;
import com.mumulcom.mumulcom.config.BaseResponseStatus;
import com.mumulcom.mumulcom.src.category.provider.CategoryProvider;
import com.mumulcom.mumulcom.src.mycategory.domain.MyCategory;
import com.mumulcom.mumulcom.src.mycategory.dto.MyCategoryDto;
import com.mumulcom.mumulcom.src.mycategory.repository.MyCategoryRepository;
import com.mumulcom.mumulcom.src.user.domain.User;
import com.mumulcom.mumulcom.src.user.dto.UserDto;
import com.mumulcom.mumulcom.src.user.repository.UserRepository;
import com.mumulcom.mumulcom.utils.JwtService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Transactional(rollbackOn = BaseException.class)
public class UserService {
    private final UserRepository userRepository;
    private final MyCategoryRepository myCategoryRepository;
    private final CategoryProvider categoryProvider;
    private final JwtService jwtService;
    private final Mapper mapper;

    public UserService(UserRepository userRepository, MyCategoryRepository myCategoryRepository, CategoryProvider categoryProvider, JwtService jwtService, Mapper mapper) {
        this.userRepository = userRepository;
        this.myCategoryRepository = myCategoryRepository;
        this.categoryProvider = categoryProvider;
        this.jwtService = jwtService;
        this.mapper = mapper;
    }

    /**
     * 회원가입
     */
    public UserDto.SignUpRes join(UserDto.SignUpReq signUpReq) throws BaseException {
        if (userRepository.existsUserByEmailAndStatus(signUpReq.getEmail(), "active")) {
            throw new BaseException(BaseResponseStatus.POST_USERS_EXISTS_EMAIL);
        }
        if (userRepository.existsUserByNickname(signUpReq.getNickname())) {
            throw new BaseException(BaseResponseStatus.POST_USERS_EXISTS_NICKNAME);
        }
        User mappedUser = mapper.map(signUpReq, User.class);
        mappedUser.updateProfileImgUrlRandomly();
        User user = userRepository.save(mappedUser);
        if (signUpReq.getMyCategories() != null) {
            for (String smallCategoryName : signUpReq.getMyCategories()) {
                Optional<Long> smallCategoryIdx = categoryProvider.getSmallCategoryIdxByName(smallCategoryName);
                if (smallCategoryIdx.isEmpty()) {
                    throw new BaseException(BaseResponseStatus.POST_USERS_INVALID_CATEGORY);
                }
                MyCategoryDto myCategoryDto = MyCategoryDto.builder()
                        .userIdx(user.getUserIdx())
                        .smallCategoryIdx(smallCategoryIdx.get())
                        .build();
                myCategoryRepository.save(mapper.map(myCategoryDto, MyCategory.class));
            }
        }
        String jwt = jwtService.createJwt(user.getUserIdx());
        return UserDto.SignUpRes.builder()
                .jwt(jwt)
                .userIdx(user.getUserIdx())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .group(user.getGroup())
                .myCategories(getMyCategoriesByUserIdx(user.getUserIdx()))
                .profileImgUrl(user.getProfileImgUrl())
                .build();
    }

    /**
     * 로그인
     */
    public UserDto.SignInRes login(UserDto.SignInReq signInReq) throws BaseException {
        String email = signInReq.getEmail();
        Optional<User> userOptional = userRepository.findUserByEmailAndStatus(email, "active");
        if (userOptional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        User user = userOptional.get();
        if (user.getStatus().equals("inactive")) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        user.updateFcmToken(signInReq.getFcmToken());
        String jwt = jwtService.createJwt(user.getUserIdx());
        return UserDto.SignInRes.builder()
                .jwt(jwt)
                .userIdx(user.getUserIdx())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .group(user.getGroup())
                .myCategories(getMyCategoriesByUserIdx(user.getUserIdx()))
                .profileImgUrl(user.getProfileImgUrl())
                .fcmToken(user.getFcmToken())
                .build();
    }

    /**
     * 회원정보 조회
     */
    public UserDto.UserRes getUser(Long userIdx) throws BaseException {
        Optional<User> userOptional = userRepository.findById(userIdx);
        if (userOptional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.RESPONSE_ERROR);
        }
        User user = userOptional.get();
        return UserDto.UserRes.builder()
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .group(user.getGroup())
                .myCategories(getMyCategoriesByUserIdx(user.getUserIdx()))
                .profileImgUrl(user.getProfileImgUrl())
                .build();
    }

    /**
     * 회원정보 수정
     */
    public UserDto.UserRes updateUser(UserDto.PatchReq patchReq) throws BaseException {
        Optional<User> userOptional = userRepository.findById(patchReq.getUserIdx());
        //요청에서 받은 userIdx에 해당하는 User가 없을 때
        if (userOptional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.RESPONSE_ERROR);
        }
        User user = userOptional.get();
        //닉네임을 변경하고자 하는 경우
        if (!user.getNickname().equals(patchReq.getNickname())) {
            //변경할 닉네임이 이미 사용 중인지 검사
            if (existsByNickname(patchReq.getNickname())) {
                throw new BaseException(BaseResponseStatus.POST_USERS_EXISTS_NICKNAME);
            }
            //마지막 닉네임 변경일로부터 14일이 지났는지 검사
            final int PERIOD_FOR_NICKNAME = 14;
            LocalDateTime nicknameUpdatedAt = user.getNicknameUpdatedAt();
            if (ChronoUnit.DAYS.between(nicknameUpdatedAt, LocalDateTime.now()) < PERIOD_FOR_NICKNAME) {
                throw new BaseException(BaseResponseStatus.PATCH_USERS_INVALID_NICKNAME_PERIOD);
            }
        }
        user.updateUserInfo(
                patchReq.getNickname(),
                patchReq.getGroup(),
                patchReq.getProfileImgUrl()
        );
        //관심 코딩 분야에서 변경된 사항이 있으면
        if (patchReq.getMyCategories() != null && isMyCategoriesChanged(getMyCategoriesByUserIdx(user.getUserIdx()), patchReq.getMyCategories())) {
            //기존 레코드 삭제
            myCategoryRepository.deleteAll(myCategoryRepository.findByUserIdx(user.getUserIdx()));
            //새로 저장
            for (String categoryName : patchReq.getMyCategories()) {
                categoryProvider.getSmallCategoryIdxByName(categoryName).ifPresent(smallCategoryIdx ->
                {
                    MyCategoryDto myCategoryDto = MyCategoryDto.builder()
                            .userIdx(user.getUserIdx())
                            .smallCategoryIdx(smallCategoryIdx)
                            .build();
                    myCategoryRepository.save(mapper.map(myCategoryDto, MyCategory.class));
                });
            }
        }

        return UserDto.UserRes.builder()
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .group(user.getGroup())
                .myCategories(getMyCategoriesByUserIdx(user.getUserIdx()))
                .profileImgUrl(user.getProfileImgUrl())
                .build();
    }

    /**
     * 회원 탈퇴
     */
    public UserDto.UserRes deleteUser(Long userIdx) throws BaseException {
        Optional<User> userOptional = userRepository.findById(userIdx);
        if (userOptional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.RESPONSE_ERROR);
        }
        User user = userOptional.get();
        user.deleteUser();
        return UserDto.UserRes.builder()
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .group(user.getGroup())
                .myCategories(getMyCategoriesByUserIdx(user.getUserIdx()))
                .profileImgUrl(user.getProfileImgUrl())
                .build();
    }

    /**
     * 닉네임 중복 여부 확인
     */
    public boolean existsByNickname(String nickname) {
        if (nickname.equals("알 수 없음")) {
            return true;
        }
        return userRepository.existsUserByNickname(nickname);
    }

    /**
     * 관심 코딩 분야 변경 여부 확인
     */
    public boolean isMyCategoriesChanged(List<String> list1, List<String> list2) {
        return !(list1.containsAll(list2) && list2.containsAll(list1));
    }

    /**
     * userIdx 를 통해 관심 코딩 분야 리스트 반환 (카테고리명)
     */
    public List<String> getMyCategoriesByUserIdx(Long userIdx) throws BaseException {
        List<String> myCategories = new ArrayList<>();
        for (MyCategory myCategory : myCategoryRepository.findByUserIdx(userIdx)) {
            categoryProvider.getSmallCategoryNameByIdx(myCategory.getSmallCategoryIdx()).ifPresent(myCategories::add);
        }
        return myCategories;
    }
}
