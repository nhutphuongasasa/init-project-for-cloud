// src/lib/axios.ts
import axios from "axios";

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL,
  withCredentials: true,
});

// Bắt 401 → xóa cookie + về trang chủ
api.interceptors.response.use(
  (res) => res,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.clear();

      axios.get("http://localhost:8000/logout", {
        withCredentials: true
      });
      
      // if (window.location.pathname !== "/") {
      //   window.location.href = "/";
      // }
    }
    return Promise.reject(error);
  }
);

export default api;