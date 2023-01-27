package com.example.demo.domain;


import com.example.demo.VO.SendReq;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "TB_SEND")
public class Send {

    @Id
    private String SEND_NO; //고유키
    private String USER_NO; //사용자ID
    private String FAX_NO; //사용자팩스번호
    private String TITLE; //제목
    private String PRIVATE_INFO_YN; //개인정보 포함 여부
    private String APPR_USER_NO; //결재
    private String RESERVE_YN; //예약여부
    private String RECEIVE_FAX_NO; //받는이 팩스번호
    private String SEND_DATE; //발송일
    private String APPR_NO; //결재
    private String STATUS; //상태
    private String INSERT_DATE; //생성일시
    private String SEND_STATUS; //상태2
    private String USER_KEY; //파일과 매칭
    private String JOB_NO; //전송 성공시 발송닷컴에서 주는 NO
    private String JOB_SEQ; //임의 설정

    public Send(SendReq req) {
        this.USER_NO = req.getUserID();
        this.SEND_DATE = req.getSend_Date();
        this.USER_KEY = req.getUserKey();
        this.TITLE = req.getTitle();
        this.PRIVATE_INFO_YN = req.getPrivate_info_yn();
        this.RESERVE_YN = req.getReserve_yn();
    }

}


