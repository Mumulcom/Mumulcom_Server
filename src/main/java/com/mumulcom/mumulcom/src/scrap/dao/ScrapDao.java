package com.mumulcom.mumulcom.src.scrap.dao;


import com.mumulcom.mumulcom.src.scrap.domain.MyScrapListRes;
import com.mumulcom.mumulcom.src.scrap.dto.PostScrapReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository

public class ScrapDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }



    /**
     * 휘정
     * 스크랩한 코딩 질문 조회
     * 1. 카테고리 선택 없으면 다 보여주기
     * 2. 큰 카테고리로만 선택했을 때
     * 3. 큰 카테고리, 작은 카테고리 둘 다 선택했을 때
     * */
    // 스크랩한 코딩 질문 - 1
    public List<MyScrapListRes> myCodingScrapListRes(long userIdx, boolean isReplied) {
        String myScrapListQuery;

        if(isReplied == false) { // 답변이 달린 질문
            myScrapListQuery = "select form.userIdx, form.questionIdx, form.profileImgUrl, form.nickname, form.bigCategoryName, form.smallCategoryName, form.title, form.updatedAt, form.likeCount, form.replyCount\n" +
                    "from\n" +
                    "(select form.userIdx, form.questionIdx, form.profileImgUrl, form.nickname, form.bigCategoryName, s.smallCategoryName, form.title, form.updatedAt, form.likeCount, form.replyCount\n" +
                    "from\n" +
                    "(select q.userIdx, q.questionIdx, u.profileImgUrl, nickname, bigCategoryName, smallCategoryIdx, title, DATE_FORMAT(q.createdAt, '%m-%d, %y') AS updatedAt, likeCount, replyCount\n" +
                    "from Question q, BigCategory b, User u, CodeQuestion c ,\n" +
                    "(select q.questionIdx, ifnull(likeCount,0) as likeCount\n" +
                    "from Question q left join (select questionIdx, count(*) likeCount from QuestionLike q join User u on q.userIdx = u.userIdx where u.status = 'Active' and q.status='active' group by questionIdx) l on  q.questionIdx = l.questionIdx) l,\n" +
                    "(select q.questionIdx, ifnull(replyCount,0) as replyCount\n" +
                    "from Question q left join (select questionIdx, count(*) replyCount from Reply group by questionIdx) r on q.questionIdx = r.questionIdx) r\n" +
                    "where q.bigCategoryIdx = b.bigCategoryIdx and u.userIdx = q.userIdx and c.questionIdx = q.questionIdx\n" +
                    "and q.questionIdx = l.questionIdx and q.questionIdx = r.questionIdx) as form  left join SmallCategory s on form.smallCategoryIdx = s.smallCategoryIdx) form join Scrap s on form.questionIdx = s.questionIdx\n" +
                    "where s.userIdx =? and s.status = 'active'\n" +
                    "order by s.updatedAt desc";
        } else {
            myScrapListQuery = "select form.userIdx, form.questionIdx, form.profileImgUrl, form.nickname, form.bigCategoryName, form.smallCategoryName, form.title, form.updatedAt, form.likeCount, form.replyCount\n" +
                    "from\n" +
                    "(select form.userIdx, form.questionIdx, form.profileImgUrl, form.nickname, form.bigCategoryName, s.smallCategoryName, form.title, form.updatedAt, form.likeCount, form.replyCount\n" +
                    "from\n" +
                    "(select q.userIdx, q.questionIdx, u.profileImgUrl, nickname, bigCategoryName, smallCategoryIdx, title, DATE_FORMAT(q.createdAt, '%m-%d, %y') AS updatedAt, likeCount, replyCount\n" +
                    "from Question q, BigCategory b, User u, CodeQuestion c ,\n" +
                    "(select q.questionIdx, ifnull(likeCount,0) as likeCount\n" +
                    "from Question q left join (select questionIdx, count(*) likeCount from QuestionLike q join User u on q.userIdx = u.userIdx where u.status = 'Active' and q.status='active' group by questionIdx) l on  q.questionIdx = l.questionIdx) l,\n" +
                    "(select q.questionIdx, ifnull(replyCount,0) as replyCount\n" +
                    "from Question q left join (select questionIdx, count(*) replyCount from Reply group by questionIdx) r on q.questionIdx = r.questionIdx) r\n" +
                    "where q.bigCategoryIdx = b.bigCategoryIdx and u.userIdx = q.userIdx and c.questionIdx = q.questionIdx\n" +
                    "and q.questionIdx = l.questionIdx and q.questionIdx = r.questionIdx) as form  left join SmallCategory s on form.smallCategoryIdx = s.smallCategoryIdx) form join Scrap s on form.questionIdx = s.questionIdx\n" +
                    "where s.userIdx =? and s.status = 'active'  and replyCount <> 0 \n" +
                    "order by s.updatedAt desc";
        }

        return this.jdbcTemplate.query(myScrapListQuery,
                (rs,rowNum) -> new MyScrapListRes(
                        rs.getLong("questionIdx"),
                        rs.getString("profileImgUrl"),
                        rs.getString("nickname"),
                        rs.getString("bigCategoryName"),
                        rs.getString("smallCategoryName"),
                        rs.getString("title"),
                        rs.getString("updatedAt"),
                        rs.getInt("likeCount"),
                        rs.getInt("replyCount")
                ), userIdx);
    }


    // 스크랩한 코딩 질문 - 2
    public List<MyScrapListRes> myCodingScrapListRes(long userIdx, String bigCategoryName, boolean isReplied) {
        String myScrapListQuery;

        if(isReplied == false) { // 답변이 달린 질문
            myScrapListQuery = "select form.userIdx, form.questionIdx, form.profileImgUrl, form.nickname, form.bigCategoryName, form.smallCategoryName, form.title, form.updatedAt, form.likeCount, form.replyCount\n" +
                    "from\n" +
                    "(select form.userIdx, form.questionIdx, form.profileImgUrl, form.nickname, form.bigCategoryName, s.smallCategoryName, form.title, form.updatedAt, form.likeCount, form.replyCount\n" +
                    "from\n" +
                    "(select q.userIdx, q.questionIdx, u.profileImgUrl, nickname, bigCategoryName, smallCategoryIdx, title, DATE_FORMAT(q.createdAt, '%m-%d, %y') AS updatedAt, likeCount, replyCount\n" +
                    "from Question q, BigCategory b, User u, CodeQuestion c ,\n" +
                    "(select q.questionIdx, ifnull(likeCount,0) as likeCount\n" +
                    "from Question q left join (select questionIdx, count(*) likeCount from QuestionLike q join User u on q.userIdx = u.userIdx where u.status = 'Active' and q.status='active' group by questionIdx) l on  q.questionIdx = l.questionIdx) l,\n" +
                    "(select q.questionIdx, ifnull(replyCount,0) as replyCount\n" +
                    "from Question q left join (select questionIdx, count(*) replyCount from Reply group by questionIdx) r on q.questionIdx = r.questionIdx) r\n" +
                    "where q.bigCategoryIdx = b.bigCategoryIdx and u.userIdx = q.userIdx and c.questionIdx = q.questionIdx\n" +
                    "and q.questionIdx = l.questionIdx and q.questionIdx = r.questionIdx) as form  left join SmallCategory s on form.smallCategoryIdx = s.smallCategoryIdx) form join Scrap s on form.questionIdx = s.questionIdx\n" +
                    "where s.userIdx =? and bigCategoryName = ? and s.status = 'active'\n" +
                    "order by s.updatedAt desc";
        } else {
            myScrapListQuery = "select form.userIdx, form.questionIdx, form.profileImgUrl, form.nickname, form.bigCategoryName, form.smallCategoryName, form.title, form.updatedAt, form.likeCount, form.replyCount\n" +
                    "from\n" +
                    "(select form.userIdx, form.questionIdx, form.profileImgUrl, form.nickname, form.bigCategoryName, s.smallCategoryName, form.title, form.updatedAt, form.likeCount, form.replyCount\n" +
                    "from\n" +
                    "(select q.userIdx, q.questionIdx, u.profileImgUrl, nickname, bigCategoryName, smallCategoryIdx, title, DATE_FORMAT(q.createdAt, '%m-%d, %y') AS updatedAt, likeCount, replyCount\n" +
                    "from Question q, BigCategory b, User u, CodeQuestion c ,\n" +
                    "(select q.questionIdx, ifnull(likeCount,0) as likeCount\n" +
                    "from Question q left join (select questionIdx, count(*) likeCount from QuestionLike q join User u on q.userIdx = u.userIdx where u.status = 'Active' and q.status='active' group by questionIdx) l on  q.questionIdx = l.questionIdx) l,\n" +
                    "(select q.questionIdx, ifnull(replyCount,0) as replyCount\n" +
                    "from Question q left join (select questionIdx, count(*) replyCount from Reply group by questionIdx) r on q.questionIdx = r.questionIdx) r\n" +
                    "where q.bigCategoryIdx = b.bigCategoryIdx and u.userIdx = q.userIdx and c.questionIdx = q.questionIdx\n" +
                    "and q.questionIdx = l.questionIdx and q.questionIdx = r.questionIdx) as form  left join SmallCategory s on form.smallCategoryIdx = s.smallCategoryIdx) form join Scrap s on form.questionIdx = s.questionIdx\n" +
                    "where s.userIdx =? and bigCategoryName = ? and replyCount <> 0 and s.status = 'active'\n" +
                    "order by s.updatedAt desc";
        }
        Object[] myScrapParams = new Object[] {userIdx, bigCategoryName};
        return this.jdbcTemplate.query(myScrapListQuery,
                (rs,rowNum) -> new MyScrapListRes(
                        rs.getLong("questionIdx"),
                        rs.getString("profileImgUrl"),
                        rs.getString("nickname"),
                        rs.getString("bigCategoryName"),
                        rs.getString("smallCategoryName"),
                        rs.getString("title"),
                        rs.getString("updatedAt"),
                        rs.getInt("likeCount"),
                        rs.getInt("replyCount")
                ), myScrapParams);
    }

    // 스크랩한 코딩 질문 - 3
    public List<MyScrapListRes> myCodingScrapListRes(long userIdx, String bigCategoryName, String smallCategoryName, boolean isReplied) {
        String myScrapListQuery;

        if(isReplied == false) { // 답변이 달린 질문
            myScrapListQuery = "select form.userIdx, form.questionIdx, form.profileImgUrl, form.nickname, form.bigCategoryName, form.smallCategoryName, form.title, form.updatedAt, form.likeCount, form.replyCount\n" +
                    "from\n" +
                    "(select form.userIdx, form.questionIdx, form.profileImgUrl, form.nickname, form.bigCategoryName, s.smallCategoryName, form.title, form.updatedAt, form.likeCount, form.replyCount\n" +
                    "from\n" +
                    "(select q.userIdx, q.questionIdx, u.profileImgUrl, nickname, bigCategoryName, smallCategoryIdx, title, DATE_FORMAT(q.createdAt, '%m-%d, %y') AS updatedAt, likeCount, replyCount\n" +
                    "from Question q, BigCategory b, User u, CodeQuestion c ,\n" +
                    "(select q.questionIdx, ifnull(likeCount,0) as likeCount\n" +
                    "from Question q left join (select questionIdx, count(*) likeCount from QuestionLike q join User u on q.userIdx = u.userIdx where u.status = 'Active' and q.status='active' group by questionIdx) l on  q.questionIdx = l.questionIdx) l,\n" +
                    "(select q.questionIdx, ifnull(replyCount,0) as replyCount\n" +
                    "from Question q left join (select questionIdx, count(*) replyCount from Reply group by questionIdx) r on q.questionIdx = r.questionIdx) r\n" +
                    "where q.bigCategoryIdx = b.bigCategoryIdx and u.userIdx = q.userIdx and c.questionIdx = q.questionIdx\n" +
                    "and q.questionIdx = l.questionIdx and q.questionIdx = r.questionIdx) as form  left join SmallCategory s on form.smallCategoryIdx = s.smallCategoryIdx) form join Scrap s on form.questionIdx = s.questionIdx\n" +
                    "where s.userIdx =? and bigCategoryName = ? and smallCategoryName = ? and s.status = 'active'\n" +
                    "order by s.updatedAt desc";
        } else {
            myScrapListQuery = "select form.userIdx, form.questionIdx, form.profileImgUrl, form.nickname, form.bigCategoryName, form.smallCategoryName, form.title, form.updatedAt, form.likeCount, form.replyCount\n" +
                    "from\n" +
                    "(select form.userIdx, form.questionIdx, form.profileImgUrl, form.nickname, form.bigCategoryName, s.smallCategoryName, form.title, form.updatedAt, form.likeCount, form.replyCount\n" +
                    "from\n" +
                    "(select q.userIdx, q.questionIdx, u.profileImgUrl, nickname, bigCategoryName, smallCategoryIdx, title, DATE_FORMAT(q.createdAt, '%m-%d, %y') AS updatedAt, likeCount, replyCount\n" +
                    "from Question q, BigCategory b, User u, CodeQuestion c ,\n" +
                    "(select q.questionIdx, ifnull(likeCount,0) as likeCount\n" +
                    "from Question q left join (select questionIdx, count(*) likeCount from QuestionLike q join User u on q.userIdx = u.userIdx where u.status = 'Active' and q.status='active' group by questionIdx) l on  q.questionIdx = l.questionIdx) l,\n" +
                    "(select q.questionIdx, ifnull(replyCount,0) as replyCount\n" +
                    "from Question q left join (select questionIdx, count(*) replyCount from Reply group by questionIdx) r on q.questionIdx = r.questionIdx) r\n" +
                    "where q.bigCategoryIdx = b.bigCategoryIdx and u.userIdx = q.userIdx and c.questionIdx = q.questionIdx\n" +
                    "and q.questionIdx = l.questionIdx and q.questionIdx = r.questionIdx) as form  left join SmallCategory s on form.smallCategoryIdx = s.smallCategoryIdx) form join Scrap s on form.questionIdx = s.questionIdx\n" +
                    "where s.userIdx =? and bigCategoryName = ? and smallCategoryName = ? and replyCount <> 0 and s.status = 'active'\n" +
                    "order by s.updatedAt desc";
        }

        Object[] myScrapParams = new Object[] {userIdx, bigCategoryName, smallCategoryName};
        return this.jdbcTemplate.query(myScrapListQuery,
                (rs,rowNum) -> new MyScrapListRes(
                        rs.getLong("questionIdx"),
                        rs.getString("profileImgUrl"),
                        rs.getString("nickname"),
                        rs.getString("bigCategoryName"),
                        rs.getString("smallCategoryName"),
                        rs.getString("title"),
                        rs.getString("updatedAt"),
                        rs.getInt("likeCount"),
                        rs.getInt("replyCount")
                ), myScrapParams);
    }

    /**
     * 휘정
     * 스크랩한 개념 질문 조회
     * 1. 카테고리 선택 없으면 다 보여주기
     * 2. 큰 카테고리로만 선택했을 때
     * 3. 큰 카테고리, 작은 카테고리 둘 다 선택했을 때
     * */
    // 스크랩한 개념 질문 - 1
    public List<MyScrapListRes> myConceptScrapListRes(long userIdx, boolean isReplied) {

        String myScrapListQuery;

        if(isReplied == false) { // 답변이 달린 질문
            myScrapListQuery = "select form.userIdx, form.questionIdx, form.profileImgUrl, form.nickname, form.bigCategoryName, form.smallCategoryName, form.title, form.updatedAt, form.likeCount, form.replyCount\n" +
                    "from\n" +
                    "(select form.userIdx, form.questionIdx, form.profileImgUrl, form.nickname, form.bigCategoryName, s.smallCategoryName, form.title, form.updatedAt, form.likeCount, form.replyCount\n" +
                    "from\n" +
                    "(select q.userIdx, q.questionIdx, u.profileImgUrl, nickname, bigCategoryName, smallCategoryIdx, title, DATE_FORMAT(q.createdAt, '%m-%d, %y') AS updatedAt, likeCount, replyCount\n" +
                    "from Question q, BigCategory b, User u, ConceptQuestion c ,\n" +
                    "(select q.questionIdx, ifnull(likeCount,0) as likeCount\n" +
                    "from Question q left join (select questionIdx, count(*) likeCount from QuestionLike q join User u on q.userIdx = u.userIdx where u.status = 'Active' and q.status='active' group by questionIdx) l on  q.questionIdx = l.questionIdx) l,\n" +
                    "(select q.questionIdx, ifnull(replyCount,0) as replyCount\n" +
                    "from Question q left join (select questionIdx, count(*) replyCount from Reply group by questionIdx) r on q.questionIdx = r.questionIdx) r\n" +
                    "where q.bigCategoryIdx = b.bigCategoryIdx and u.userIdx = q.userIdx and c.questionIdx = q.questionIdx\n" +
                    "and q.questionIdx = l.questionIdx and q.questionIdx = r.questionIdx) as form  left join SmallCategory s on form.smallCategoryIdx = s.smallCategoryIdx) form join Scrap s on form.questionIdx = s.questionIdx\n" +
                    "where s.userIdx =? and s.status = 'active'\n" +
                    "order by s.updatedAt desc";
        } else {
            myScrapListQuery = "select form.userIdx, form.questionIdx, form.profileImgUrl, form.nickname, form.bigCategoryName, form.smallCategoryName, form.title, form.updatedAt, form.likeCount, form.replyCount\n" +
                    "from\n" +
                    "(select form.userIdx, form.questionIdx, form.profileImgUrl, form.nickname, form.bigCategoryName, s.smallCategoryName, form.title, form.updatedAt, form.likeCount, form.replyCount\n" +
                    "from\n" +
                    "(select q.userIdx, q.questionIdx, u.profileImgUrl, nickname, bigCategoryName, smallCategoryIdx, title, DATE_FORMAT(q.createdAt, '%m-%d, %y') AS updatedAt, likeCount, replyCount\n" +
                    "from Question q, BigCategory b, User u, ConceptQuestion c ,\n" +
                    "(select q.questionIdx, ifnull(likeCount,0) as likeCount\n" +
                    "from Question q left join (select questionIdx, count(*) likeCount from QuestionLike q join User u on q.userIdx = u.userIdx where u.status = 'Active' and q.status='active' group by questionIdx) l on  q.questionIdx = l.questionIdx) l,\n" +
                    "(select q.questionIdx, ifnull(replyCount,0) as replyCount\n" +
                    "from Question q left join (select questionIdx, count(*) replyCount from Reply group by questionIdx) r on q.questionIdx = r.questionIdx) r\n" +
                    "where q.bigCategoryIdx = b.bigCategoryIdx and u.userIdx = q.userIdx and c.questionIdx = q.questionIdx\n" +
                    "and q.questionIdx = l.questionIdx and q.questionIdx = r.questionIdx) as form  left join SmallCategory s on form.smallCategoryIdx = s.smallCategoryIdx) form join Scrap s on form.questionIdx = s.questionIdx\n" +
                    "where s.userIdx =? and s.status = 'active' and replyCount <> 0 \n" +
                    "order by s.updatedAt desc";
        }

        return this.jdbcTemplate.query(myScrapListQuery,
                (rs,rowNum) -> new MyScrapListRes(
                        rs.getLong("questionIdx"),
                        rs.getString("profileImgUrl"),
                        rs.getString("nickname"),
                        rs.getString("bigCategoryName"),
                        rs.getString("smallCategoryName"),
                        rs.getString("title"),
                        rs.getString("updatedAt"),
                        rs.getInt("likeCount"),
                        rs.getInt("replyCount")
                ), userIdx);
    }


    // 스크랩한 개념 질문 - 2
    public List<MyScrapListRes> myConceptScrapListRes(long userIdx, String bigCategoryName, boolean isReplied) {
        String myScrapListQuery;

        if(isReplied == false) { // 답변이 달린 질문
            myScrapListQuery = "select form.userIdx, form.questionIdx, form.profileImgUrl, form.nickname, form.bigCategoryName, form.smallCategoryName, form.title, form.updatedAt, form.likeCount, form.replyCount\n" +
                    "from\n" +
                    "(select form.userIdx, form.questionIdx, form.profileImgUrl, form.nickname, form.bigCategoryName, s.smallCategoryName, form.title, form.updatedAt, form.likeCount, form.replyCount\n" +
                    "from\n" +
                    "(select q.userIdx, q.questionIdx, u.profileImgUrl, nickname, bigCategoryName, smallCategoryIdx, title, DATE_FORMAT(q.createdAt, '%m-%d, %y') AS updatedAt, likeCount, replyCount\n" +
                    "from Question q, BigCategory b, User u, ConceptQuestion c ,\n" +
                    "(select q.questionIdx, ifnull(likeCount,0) as likeCount\n" +
                    "from Question q left join (select questionIdx, count(*) likeCount from QuestionLike q join User u on q.userIdx = u.userIdx where u.status = 'Active' and q.status='active' group by questionIdx) l on  q.questionIdx = l.questionIdx) l,\n" +
                    "(select q.questionIdx, ifnull(replyCount,0) as replyCount\n" +
                    "from Question q left join (select questionIdx, count(*) replyCount from Reply group by questionIdx) r on q.questionIdx = r.questionIdx) r\n" +
                    "where q.bigCategoryIdx = b.bigCategoryIdx and u.userIdx = q.userIdx and c.questionIdx = q.questionIdx\n" +
                    "and q.questionIdx = l.questionIdx and q.questionIdx = r.questionIdx) as form  left join SmallCategory s on form.smallCategoryIdx = s.smallCategoryIdx) form join Scrap s on form.questionIdx = s.questionIdx\n" +
                    "where s.userIdx =? and s.status = 'active' and bigCategoryName = ?\n" +
                    "order by s.updatedAt desc";
        } else {
            myScrapListQuery = "select form.userIdx, form.questionIdx, form.profileImgUrl, form.nickname, form.bigCategoryName, form.smallCategoryName, form.title, form.updatedAt, form.likeCount, form.replyCount\n" +
                    "from\n" +
                    "(select form.userIdx, form.questionIdx, form.profileImgUrl, form.nickname, form.bigCategoryName, s.smallCategoryName, form.title, form.updatedAt, form.likeCount, form.replyCount\n" +
                    "from\n" +
                    "(select q.userIdx, q.questionIdx, u.profileImgUrl, nickname, bigCategoryName, smallCategoryIdx, title, DATE_FORMAT(q.createdAt, '%m-%d, %y') AS updatedAt, likeCount, replyCount\n" +
                    "from Question q, BigCategory b, User u, ConceptQuestion c ,\n" +
                    "(select q.questionIdx, ifnull(likeCount,0) as likeCount\n" +
                    "from Question q left join (select questionIdx, count(*) likeCount from QuestionLike q join User u on q.userIdx = u.userIdx where u.status = 'Active' and q.status='active' group by questionIdx) l on  q.questionIdx = l.questionIdx) l,\n" +
                    "(select q.questionIdx, ifnull(replyCount,0) as replyCount\n" +
                    "from Question q left join (select questionIdx, count(*) replyCount from Reply group by questionIdx) r on q.questionIdx = r.questionIdx) r\n" +
                    "where q.bigCategoryIdx = b.bigCategoryIdx and u.userIdx = q.userIdx and c.questionIdx = q.questionIdx\n" +
                    "and q.questionIdx = l.questionIdx and q.questionIdx = r.questionIdx) as form  left join SmallCategory s on form.smallCategoryIdx = s.smallCategoryIdx) form join Scrap s on form.questionIdx = s.questionIdx\n" +
                    "where s.userIdx =? and s.status = 'active' and bigCategoryName = ? and replyCount <> 0\n" +
                    "order by s.updatedAt desc";
        }

        Object[] myScrapParams = new Object[] {userIdx, bigCategoryName};
        return this.jdbcTemplate.query(myScrapListQuery,
                (rs,rowNum) -> new MyScrapListRes(
                        rs.getLong("questionIdx"),
                        rs.getString("profileImgUrl"),
                        rs.getString("nickname"),
                        rs.getString("bigCategoryName"),
                        rs.getString("smallCategoryName"),
                        rs.getString("title"),
                        rs.getString("updatedAt"),
                        rs.getInt("likeCount"),
                        rs.getInt("replyCount")
                ), myScrapParams);
    }

    // 스크랩한 개념 질문 - 3
    public List<MyScrapListRes> myConceptScrapListRes(long userIdx, String bigCategoryName, String smallCategoryName, boolean isReplied) {
        String myScrapListQuery;

        if(isReplied == false) { // 답변이 달린 질문
            myScrapListQuery = "select form.userIdx, form.questionIdx, form.profileImgUrl, form.nickname, form.bigCategoryName, form.smallCategoryName, form.title, form.updatedAt, form.likeCount, form.replyCount\n" +
                    "from\n" +
                    "(select form.userIdx, form.questionIdx, form.profileImgUrl, form.nickname, form.bigCategoryName, s.smallCategoryName, form.title, form.updatedAt, form.likeCount, form.replyCount\n" +
                    "from\n" +
                    "(select q.userIdx, q.questionIdx, u.profileImgUrl, nickname, bigCategoryName, smallCategoryIdx, title, DATE_FORMAT(q.createdAt, '%m-%d, %y') AS updatedAt, likeCount, replyCount\n" +
                    "from Question q, BigCategory b, User u, ConceptQuestion c ,\n" +
                    "(select q.questionIdx, ifnull(likeCount,0) as likeCount\n" +
                    "from Question q left join (select questionIdx, count(*) likeCount from QuestionLike q join User u on q.userIdx = u.userIdx where u.status = 'Active' and q.status='active' group by questionIdx) l on  q.questionIdx = l.questionIdx) l,\n" +
                    "(select q.questionIdx, ifnull(replyCount,0) as replyCount\n" +
                    "from Question q left join (select questionIdx, count(*) replyCount from Reply group by questionIdx) r on q.questionIdx = r.questionIdx) r\n" +
                    "where q.bigCategoryIdx = b.bigCategoryIdx and u.userIdx = q.userIdx and c.questionIdx = q.questionIdx\n" +
                    "and q.questionIdx = l.questionIdx and q.questionIdx = r.questionIdx) as form  left join SmallCategory s on form.smallCategoryIdx = s.smallCategoryIdx) form join Scrap s on form.questionIdx = s.questionIdx\n" +
                    "where s.userIdx =? and s.status = 'active' and bigCategoryName = ? and smallCategoryName = ? \n" +
                    "order by s.updatedAt desc";
        } else {
            myScrapListQuery = "select form.userIdx, form.questionIdx, form.profileImgUrl, form.nickname, form.bigCategoryName, form.smallCategoryName, form.title, form.updatedAt, form.likeCount, form.replyCount\n" +
                    "from\n" +
                    "(select form.userIdx, form.questionIdx, form.profileImgUrl, form.nickname, form.bigCategoryName, s.smallCategoryName, form.title, form.updatedAt, form.likeCount, form.replyCount\n" +
                    "from\n" +
                    "(select q.userIdx, q.questionIdx, u.profileImgUrl, nickname, bigCategoryName, smallCategoryIdx, title, DATE_FORMAT(q.createdAt, '%m-%d, %y') AS updatedAt, likeCount, replyCount\n" +
                    "from Question q, BigCategory b, User u, ConceptQuestion c ,\n" +
                    "(select q.questionIdx, ifnull(likeCount,0) as likeCount\n" +
                    "from Question q left join (select questionIdx, count(*) likeCount from QuestionLike q join User u on q.userIdx = u.userIdx where u.status = 'Active' and q.status='active' group by questionIdx) l on  q.questionIdx = l.questionIdx) l,\n" +
                    "(select q.questionIdx, ifnull(replyCount,0) as replyCount\n" +
                    "from Question q left join (select questionIdx, count(*) replyCount from Reply group by questionIdx) r on q.questionIdx = r.questionIdx) r\n" +
                    "where q.bigCategoryIdx = b.bigCategoryIdx and u.userIdx = q.userIdx and c.questionIdx = q.questionIdx\n" +
                    "and q.questionIdx = l.questionIdx and q.questionIdx = r.questionIdx) as form  left join SmallCategory s on form.smallCategoryIdx = s.smallCategoryIdx) form join Scrap s on form.questionIdx = s.questionIdx\n" +
                    "where s.userIdx =? and s.status = 'active' and bigCategoryName = ? and smallCategoryName = ? and replyCount <> 0 \n" +
                    "order by s.updatedAt desc";
        }

        Object[] myScrapParams = new Object[] {userIdx, bigCategoryName, smallCategoryName};
        return this.jdbcTemplate.query(myScrapListQuery,
                (rs,rowNum) -> new MyScrapListRes(
                        rs.getLong("questionIdx"),
                        rs.getString("profileImgUrl"),
                        rs.getString("nickname"),
                        rs.getString("bigCategoryName"),
                        rs.getString("smallCategoryName"),
                        rs.getString("title"),
                        rs.getString("updatedAt"),
                        rs.getInt("likeCount"),
                        rs.getInt("replyCount")
                ), myScrapParams);
    }


    //학준24.1
    public String createScrap(PostScrapReq postScrapReq, int scrapStatus ){
        Object[] ScrapParams = new Object[]{postScrapReq.getQuestionIdx(),
                postScrapReq.getUserIdx()};
        switch(scrapStatus) {
            case 1:
                String createScrapQuery = "INSERT INTO Scrap(QUESTIONIDX, USERIDX) VALUES (?, ?)";
                this.jdbcTemplate.update(createScrapQuery,ScrapParams);
                return new String("해당 글을 스크랩 하였습니다.");
            case 2:
                String changeToInactiveQuery = "update Scrap\n" +
                        "SET status = 'inactive'\n" +
                        "where questionIdx = ? AND userIdx = ?";
                this.jdbcTemplate.update(changeToInactiveQuery,ScrapParams);
                return new String("해당 글을 스크랩 취소하였습니다.");
            default:
                String changeToActiveQuery =  "update Scrap\n" +
                        "SET status = 'active'\n" +
                        "where questionIdx = ? AND userIdx = ?";
                this.jdbcTemplate.update(changeToActiveQuery,ScrapParams);
                return new String("해당 글을 다시 스크랩 하였습니다.");

        }



    }


    //24.-2
    public int scrapAuth(PostScrapReq postScrapReq){
        String scrapAuthQuery = "select userIdx\n" +
                "from Question\n" +
                "where questionIdx = ?";
         if(this.jdbcTemplate.queryForObject(scrapAuthQuery, long.class,
                postScrapReq.getQuestionIdx()) == postScrapReq.getUserIdx()){
             return 0;
         }else
             return 1;

    }

    //24-3
    public int queIdExist(Long getQuestionIdx){
        String checkQueQuery = "SELECT EXISTS(SELECT questionIdx from Question where  " +
                "questionIdx =?)";

        return this.jdbcTemplate.queryForObject(checkQueQuery, int.class,
                getQuestionIdx);
    }

    //학준 24.4
    public int getScrapStatus(PostScrapReq postScrapReq){
        String scrapStatusQuery  = "SELECT\n" +
                "        distinct CASE\n" +
                "            WHEN EXISTS(SELECT userIdx, questionIdx from Scrap where userIdx = ? AND questionIdx = ? AND status = 'active')= '1' then 2\n" +
                "            WHEN EXISTS(SELECT userIdx, questionIdx from Scrap where userIdx = ? AND questionIdx = ? AND status = 'inactive')= '1' then 3\n" +
                "            ELSE 1 END\n" +
                "FROM Scrap s\n" +
                "right join Question q on s.questionIdx = q.questionIdx ";


        Object[] scrapStatusParams = new Object[]{postScrapReq.getUserIdx(), postScrapReq.getQuestionIdx(),
        postScrapReq.getUserIdx(),postScrapReq.getQuestionIdx()};
        return this.jdbcTemplate.queryForObject(scrapStatusQuery, int.class,scrapStatusParams);
    }


}
