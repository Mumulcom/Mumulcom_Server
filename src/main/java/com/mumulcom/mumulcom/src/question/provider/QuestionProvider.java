package com.mumulcom.mumulcom.src.question.provider;

//import com.example.demo.src.user_1.model.*;
//import com.example.demo.utils.JwtService;
import com.mumulcom.mumulcom.config.BaseException;
import com.mumulcom.mumulcom.config.BaseResponseStatus;
import com.mumulcom.mumulcom.config.BaseResponseStatus.*;
import com.mumulcom.mumulcom.src.question.dao.QuestionDao;
import com.mumulcom.mumulcom.src.question.domain.MyQuestionListRes;
import com.mumulcom.mumulcom.src.question.domain.SearchCodingQuestionRes;
import com.mumulcom.mumulcom.src.question.domain.SearchConceptQuestionRes;
import com.mumulcom.mumulcom.src.question.dto.GetRecQueRes;
import com.mumulcom.mumulcom.utils.JwtService;
        import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.mumulcom.mumulcom.config.BaseResponseStatus.DATABASE_ERROR;
//import com.example.demo.utils.*;
//import com.example.demo.config.secret.*;


@Service
public class QuestionProvider {

   private final QuestionDao questionDao;
   private final JwtService jwtService;

   final Logger logger = LoggerFactory.getLogger(this.getClass());

   @Autowired
    public QuestionProvider(QuestionDao questionDao, JwtService jwtService){
       this.questionDao = questionDao;
       this.jwtService = jwtService;
   }

//학준 7.
   public int checkUserStatus(long userIdx)throws BaseException {
       try {
           int validNum = questionDao.checkUserStatus(userIdx);  //active면 1, inactive면 0
           return validNum;
       } catch (Exception exception) {
           exception.printStackTrace();
           throw new BaseException(DATABASE_ERROR);
       }
   }

//학준 9. 최근질문 최대 4개 조회
public List<GetRecQueRes> getRecQuestion(long userIdx) throws BaseException{
    try{

      List<GetRecQueRes> getRecQueRes = questionDao.getRecQuestion( userIdx);
      return getRecQueRes;
    } catch (Exception exception){
      exception.printStackTrace();
      throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
   }
}




   //학준 16
   public List<GetRecQueRes> getRecQuestions(long userIdx) throws BaseException{

      try{
         List<GetRecQueRes> getRecQueRes = questionDao.getRecQuestions(userIdx);
         return getRecQueRes;
      }catch (Exception exception){
         exception.printStackTrace();
         throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
      }
   }

   /**
    * 휘정
    * 개념 질문 검색하기
    * */
   // keyword 없을 때
   public List<SearchConceptQuestionRes> searchConceptQuestionResList() throws BaseException{
       try {
           List<SearchConceptQuestionRes> searchConceptQuestionResList = questionDao.searchConceptQuestionRes();
           return searchConceptQuestionResList;
       } catch (Exception exception) {
           exception.printStackTrace();
           throw new BaseException(DATABASE_ERROR);
       }
   }

    //keyword 있을 때
   public List<SearchConceptQuestionRes> searchConceptQuestionResList(String keyword) throws BaseException {
      try {
         List<SearchConceptQuestionRes> searchConceptQuestionResList = questionDao.searchConceptQuestionRes(keyword);
         return searchConceptQuestionResList;
      } catch (Exception exception) {
         exception.printStackTrace();
         throw new BaseException(DATABASE_ERROR);
      }
   }

   /**
    * 휘정
    * 코딩 질문 검색하기
    * */
   //keyword 없을 때
   public List<SearchCodingQuestionRes> searchCodingQuestionResList() throws BaseException {
       try {
           List<SearchCodingQuestionRes> searchCodingQuestionResList = questionDao.searchCodingQuestionRes();
           return searchCodingQuestionResList;
       } catch (Exception exception) {
           exception.printStackTrace();
           throw new BaseException(DATABASE_ERROR);
       }
   }

   //keyword 있을 때
   public List<SearchCodingQuestionRes> searchCodingQuestionResList(String keyword) throws BaseException {
      try {
         List<SearchCodingQuestionRes> searchCodingQuestionResList = questionDao.searchCodingQuestionRes(keyword);
         return searchCodingQuestionResList;
      } catch (Exception exception) {
         exception.printStackTrace();
         throw new BaseException(DATABASE_ERROR);
      }
   }

   /**
    * 휘정
    * 내가 한 질문 목록 받아오는 API
    * */

   // 코딩 질문 목록
   public List<MyQuestionListRes> myCodingQuestionListResList (long userIdx, boolean isReplied) throws BaseException {
      try {
         List<MyQuestionListRes> myQuestionListResList = questionDao.myCodingQuestionListRes(userIdx, isReplied);
         return myQuestionListResList;
      } catch (Exception exception) {
         exception.printStackTrace();
         throw new BaseException(DATABASE_ERROR);
      }
   }

   // 개념 질문 목록
   public List<MyQuestionListRes> myConceptQuestionListResList (long userIdx, boolean isReplied) throws BaseException {
      try {
         List<MyQuestionListRes> myQuestionListResList = questionDao.myConceptQuestionListRes(userIdx,isReplied);
         return myQuestionListResList;
      } catch (Exception exception) {
         exception.printStackTrace();
         throw new BaseException(DATABASE_ERROR);
      }
   }

}
