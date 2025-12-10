
// "use client"

// import { useRouter } from "next/navigation"
// import { useEffect, useState } from "react"
// import { Button } from "@/components/ui/button"
// import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"

// export default function Page() {
//   const router = useRouter()
//   const [userType, setUserType] = useState<"admin" | "vendor" | null>(null)

//   useEffect(() => {
//     // In a real app, this would check the user's role from auth
//     const storedUserType = localStorage.getItem("userType")
//     if (storedUserType) {
//       setUserType(storedUserType as "admin" | "vendor")
//     }
//   }, [])

//   const handleAdminLogin = () => {
//     localStorage.setItem("userType", "admin")
//     router.push("/admin/dashboard")
//   }

//   const handleVendorLogin = () => {
//     localStorage.setItem("userType", "vendor")
//     router.push("/vendor/dashboard")
//   }

//   return (
//     <main className="min-h-screen bg-background flex items-center justify-center p-4">
//       <div className="w-full max-w-md">
//         <div className="text-center mb-8">
//           <h1 className="text-3xl font-bold text-foreground mb-2">Warehouse Hub</h1>
//           <p className="text-muted-foreground">Multi-tenant warehouse management system</p>
//         </div>

//         <div className="grid gap-4">
//           <Card
//             className="bg-card border-border hover:border-primary/50 transition-colors cursor-pointer"
//             onClick={handleAdminLogin}
//           >
//             <CardHeader>
//               <CardTitle>Admin Portal</CardTitle>
//               <CardDescription>Manage vendors, inventory, and orders</CardDescription>
//             </CardHeader>
//             <CardContent>
//               <Button className="w-full" variant="default">
//                 Login as Admin
//               </Button>
//             </CardContent>
//           </Card>

//           <Card
//             className="bg-card border-border hover:border-accent/50 transition-colors cursor-pointer"
//             onClick={handleVendorLogin}
//           >
//             <CardHeader>
//               <CardTitle>Vendor Portal</CardTitle>
//               <CardDescription>Manage your products and inventory</CardDescription>
//             </CardHeader>
//             <CardContent>
//               <Button className="w-full bg-transparent" variant="outline">
//                 Login as Vendor
//               </Button>
//             </CardContent>
//           </Card>
//         </div>

//         <p className="text-center text-xs text-muted-foreground mt-8">Demo credentials for testing</p>
//       </div>
//     </main>
//   )
// }

// app/page.tsx
"use client";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { LogIn } from "lucide-react";

export default function Page() {
  const handleLogin = () => {
    // XÓA SẠCH session cũ trước khi login → không bao giờ bị kẹt
    document.cookie = "JSESSIONID=; path=/; expires=Thu, 01 Jan 1970 00:00:01 GMT;";
    document.cookie = "JSESSIONID=; path=/; expires=Thu, 01 Jan 1970 00:00:01 GMT; domain=localhost";
    localStorage.clear();

    window.location.href = `${process.env.NEXT_PUBLIC_API_BASE_URL}/okela`;
  };

  return (
    <main className="min-h-screen bg-background flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-foreground mb-2">Warehouse Hub</h1>
          <p className="text-muted-foreground">Multi-tenant warehouse management system</p>
        </div>

        <div className="grid gap-4">
          <div
            className="bg-card border-border rounded-lg p-6 hover:border-primary/50 transition-all cursor-pointer shadow-lg"
            onClick={handleLogin}
          >
            <CardHeader className="text-center pb-4">
              <CardTitle className="text-2xl">Đăng nhập hệ thống</CardTitle>
              <CardDescription className="text-base">
                Sử dụng tài khoản Google để đăng nhập
              </CardDescription>
            </CardHeader>
            <CardContent className="pt-4">
              <Button className="w-full h-12 text-lg" size="lg">
                <LogIn className="mr-2 h-5 w-5" />
                Đăng nhập với Google
              </Button>
            </CardContent>
          </div>
        </div>

        <p className="text-center text-xs text-muted-foreground mt-8 opacity-70">
          Mỗi lần đăng nhập sẽ là tài khoản Google mới nhất bạn chọn
        </p>
      </div>
    </main>
  );
}