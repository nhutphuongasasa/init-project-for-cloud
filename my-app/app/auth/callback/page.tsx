"use client";

import { useEffect, useState } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import axios from "axios";

export default function AuthCallback() {
  const searchParams = useSearchParams();
  const router = useRouter();
  const [status, setStatus] = useState("Đang khởi tạo chặng 3...");
  const [jwt, setJwt] = useState("");

  useEffect(() => {
    const code = searchParams.get("code");

    if (code) {
      handleExchangeCode(code);
    } else {
      setStatus("Không tìm thấy Auth Code!");
    }
  }, [searchParams]);

  const handleExchangeCode = async (authCode: string) => {
    setStatus("Đang gửi Code để đổi lấy JWT qua Body...");
    
    try {
      // Dùng URLSearchParams để gửi dạng x-www-form-urlencoded đúng chuẩn OAuth2
      const params = new URLSearchParams();
      params.append("grant_type", "authorization_code");
      params.append("code", authCode);
      params.append("redirect_uri", "http://localhost:3000/auth/callback"); // Phải khớp với Backend
      params.append("client_id", "warehouse-client"); // ID bạn đăng ký trong DB

      // GỌI CHẶNG 3: POST trực tiếp vào Auth Server
      const response = await axios.post("http://localhost:8005/oauth2/token", params, {
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
          // Nếu bạn có Client Secret, dùng Basic Auth:
          // "Authorization": "Basic " + btoa("warehouse-client:secret")
        },
      });

      // KẾT QUẢ: JWT nằm trong Body response
      const accessToken = response.data.access_token;
      setJwt(accessToken);
      setStatus("Thành công! JWT đã được nhận qua Response Body.");

      // Lưu vào máy để dùng cho các trang sau
      localStorage.setItem("token", accessToken);
      
      console.log("JWT Của Bạn Đây:", accessToken);

    } catch (error: any) {
      console.error(error);
      setStatus("Lỗi Chặng 3: " + (error.response?.data?.error_description || error.message));
    }
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen p-10 bg-slate-900 text-white">
      <h2 className="text-xl mb-4 text-cyan-400">{status}</h2>
      
      {jwt && (
        <div className="w-full max-w-2xl bg-black p-6 rounded-lg border border-cyan-500 shadow-2xl">
          <p className="text-sm font-bold text-gray-400 mb-2 underline">Mã JWT nhận được:</p>
          <p className="break-all font-mono text-xs text-yellow-200">{jwt}</p>
          <button 
            onClick={() => router.push("/dashboard")}
            className="mt-6 px-4 py-2 bg-cyan-600 rounded hover:bg-cyan-500"
          >
            Vào Dashboard
          </button>
        </div>
      )}
    </div>
  );
}