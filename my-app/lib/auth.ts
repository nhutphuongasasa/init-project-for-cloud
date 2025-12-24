// // src/lib/auth.ts
// import api from "./axios";

// // Hàm kiểm tra quyền: thử gọi API admin trước → nếu được thì là admin, không thì vendor
// export const detectUserType = async (): Promise<"admin" | "vendor"> => {
//   try {
//     // Thử gọi API chỉ Admin mới vào được
//     await api.get("/api/vendor/admin/pending");
//     return "admin";
//   } catch (err: any) {
//     if (err.response?.status === 403 || err.response?.status === 401) {
//       // Nếu bị 403 → là vendor (vì vendor không có quyền vào /admin)
//       return "vendor";
//     }
//     throw err;
//   }
// };