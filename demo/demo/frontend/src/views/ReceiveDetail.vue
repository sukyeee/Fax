<template>
  <div class="modal-container">
    <div class="display-4 mb-4 font-weight-800 text-default ml-2">부서 팩스함</div>
    <div class="modal-container-detail">
      <div class="left-content col">
        <span class="mt-3"><i class="fa fa-caret-right" aria-hidden="true"></i> 보낸 곳</span>

        <table class="fax-table fax-table-detail" style="width: 100%">
          <thead>
            <tr>
              <th scope="col">팩스번호</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>{{ receivelistDetail.sender_NO }}</td>
            </tr>
          </tbody>
        </table>

        <span class="mt-3"><i class="fa fa-caret-right" aria-hidden="true"></i> 받는곳</span>
        <table class="fax-table fax-table-detail">
          <thead>
            <tr>
              <th scope="col">부서명</th>
              <th scope="col">팩스번호</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>{{ this.userInfo.deptName }}</td>
              <td>{{ receivelistDetail.fax_NO }}</td>
            </tr>
          </tbody>
        </table>

        <span class="mt-3"><i class="fa fa-caret-right" aria-hidden="true"></i> 수신 정보</span>
        <table class="fax-table fax-table-detail">
          <colgroup>
            <col style="width: 50%" />
            <col style="width: 25%" />
            <col style="width: 25%" />
          </colgroup>
          <thead>
            <tr>
              <th scope="col">수신일시</th>
              <th scope="col">장수</th>
              <th scope="col">수신상태</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>{{ receivelistDetail.receive_DATE }}</td>
              <td>{{ receivelistDetail.page_CNT }}</td>
              <td>{{ receivelistDetail.Result }}</td>
            </tr>
          </tbody>
        </table>

        <span class="mt-3 mb-1"><i class="fa fa-caret-right" aria-hidden="true"></i> 제목</span>
        <div class="row ml-1">
          <!-- <base-input
            class="fax-table-input"
            style="height: 30px; width: 15rem"
            v-model="receivelistDetail.title"
          >
          </base-input> -->
          <input
            type="text"
            class="fax-form-input"
            style="height: 2.5rem; width: 15rem"
            v-model="receivelistDetail.title"
          />
          <base-button
            type="secondary"
            class="receive-detail-btn ml-1 py-2 px-3"
            @click="savetitle"
          >
            제목저장
          </base-button>
        </div>
      </div>

      <div class="right-content col col-8" v-if="receivelistDetail.filePath">
        <iframe
          v-bind:src="`https://bnksys.s3.ap-northeast-2.amazonaws.com/${receivelistDetail.filePath}`"
          style="width: 100%; height: 100%"
        ></iframe>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import http from "@/common/axios.js";
import alertify from "alertifyjs";

export default {
  name: "receive_detail",
  props: ["receivelistDetail"],
  computed: {
    ...mapGetters({
      userInfo: "getUserInfo",
    }),
  },
  data() {
    return {
      title: "",
      RFax_No_Seq: "",
    };
  },
  updated() {
    //상세에서 props 넘긴 후 호출
    this.title = this.receivelistDetail.title;
    this.RFax_No_Seq = this.receivelistDetail.receive_No_SEQ;
  },
  methods: {
    //제목 값세팅
    getTitle: function () {
      if (this.title == "") {
        this.title = this.receivelistDetail.title;
      }
      return this.title;
    },
    setTitle: function (value) {
      this.title = value;
    },
    // 제목저장
    async savetitle() {
      this.$store.commit("SET_LOADING_TRUE");
      console.log(this.title);
      console.log(this.RFax_No_Seq);
      try {
        let response = await http.post("/titleSave", {
          RFax_No_Seq: this.RFax_No_Seq,
          Title: this.title,
        });

        let { data } = response;
        this.$store.commit("SET_LOADING_FALSE");

        if (data != null) {
          // 전송 성공
          console.log(data);
          console.log("전송 성공");
          alertify.success("제목 저장이 완료되었습니다.", 1.5);
        } else {
          console.log("전송 실패");
        }
      } catch (error) {
        // 전송 실패
        console.log("오류메시지 - ", data.Message);
        alertify.error("실패했습니다.", 1.5);
      }
    },
  },
};
</script>

<style scoped>
.receive-detail-btn {
  width: fit-content;
  height: fit-content;
}
@media screen and (max-width: 991px) {
  .modal-container {
  }
  .text-default {
    text-align: center;
  }
  .modal-container-detail {
    display: flex;
    flex-direction: column;
  }
  .left-content {
    margin-top: 1rem;
    flex-basis: auto;
    order: 1;
  }
  .right-content {
    max-width: fit-content;
    max-height: 100vh;
    order: 0;
  }
  .receive-detail-btn-group {
    display: flex;
  }
  .fax-form-input {
    width: 12rem !important;
  }
  .receive-detail-btn {
    padding: 10px;
  }
}
</style>
