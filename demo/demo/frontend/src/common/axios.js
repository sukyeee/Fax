import axios from "axios";

// axios 객체 생성 export
export default axios.create({
    // 백엔드 localhost:8080
    baseURL: 'http://158.247.194.215:8080/api/',
    headers: {
        'Content-Type': 'application/json'
    },
    
    withCredentials: true
});
