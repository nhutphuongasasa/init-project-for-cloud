"use client";

import { useState,  useEffect } from "react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import { Search, RefreshCw, CheckCircle, Clock, Ban } from "lucide-react";
import { Header } from "@/components/custom/header";
import axios from "axios";

interface Vendor {
  id: string;
  name: string;
  slug: string;
  description: string;
  logoUrl: string;
  status: "ACTIVE" | "PENDING" | "SUSPENDED";
  joinedAt: string;
}

const statusConfig = {
  ACTIVE: { icon: CheckCircle, color: "text-green-500", bg: "bg-green-500/10", label: "Đã duyệt" },
  PENDING: { icon: Clock, color: "text-yellow-500", bg: "bg-yellow-500/10", label: "Chờ duyệt" },
  SUSPENDED: { icon: Ban, color: "text-red-500", bg: "bg-red-500/10", label: "Bị khóa" },
};

export default function VendorsPage() {
  const [vendors, setVendors] = useState<Vendor[]>([]);
  const [filteredVendors, setFilteredVendors] = useState<Vendor[]>([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [loading, setLoading] = useState(true);
  const [approvingId, setApprovingId] = useState<string | null>(null);
  const [mode, setMode] = useState<"all" | "pending">("all");

  const fetchVendors = async (fetchMode: "all" | "pending") => {
    try {
      setLoading(true);
      let url = "";
      if (fetchMode === "pending") {
        url = `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/vendor/admin/pending`;
      } else {
        url = `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/vendor/admin/all?page=0&size=20`;
      }

      const res = await axios.get(url, { withCredentials: true });

      // Xử lý 2 kiểu response khác nhau
      let rawData: any[] = [];

      if (fetchMode === "pending") {
        // Kiểu 1: trả mảng thẳng trong res.data
        rawData = Array.isArray(res.data) ? res.data : res.data?.data || [];
      } else {
        // Kiểu 2: có phân trang → content nằm trong res.data.data.content
        rawData = res.data?.data?.content || res.data?.content || [];
      }

      console.log("Raw data sau khi xử lý:", rawData);

      const mapped: Vendor[] = rawData.map((v: any) => ({
        id: v.id,
        name: v.name || "Chưa đặt tên",
        slug: v.slug || "",
        description: v.description || "",
        logoUrl: v.logoUrl || "",
        status: v.status || "PENDING",
        joinedAt: v.joinedAt || v.createdAt || new Date().toISOString(),
      }));

      setVendors(mapped);
      setFilteredVendors(mapped);
    } catch (err: any) {
      console.error("Lỗi fetch:", err);
      alert(err.response?.data?.message || "Lỗi kết nối API");
      setVendors([]);
      setFilteredVendors([]);
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (vendorId: string) => {
    if (!confirm("Phê duyệt nhà bán này?")) return;

    try {
      setApprovingId(vendorId);
      await axios.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/vendor/admin/approve/${vendorId}`,
        {},
        { withCredentials: true }
      );

      // Refresh lại danh sách hiện tại
      fetchVendors(mode);
      alert("Phê duyệt thành công!");
    } catch (err: any) {
      alert(err.response?.data?.message || "Phê duyệt thất bại");
    } finally {
      setApprovingId(null);
    }
  };

  useEffect(() => {
    const term = searchTerm.toLowerCase();
    const filtered = vendors.filter(v =>
      v.name.toLowerCase().includes(term) ||
      v.slug.toLowerCase().includes(term) ||
      v.description.toLowerCase().includes(term)
    );
    setFilteredVendors(filtered);
  }, [searchTerm, vendors]);

  useEffect(() => {
    fetchVendors("all"); // Load lần đầu: tất cả
  }, []);

  const formatDate = (dateStr: string) => {
    try {
      return new Date(dateStr).toLocaleString("vi-VN");
    } catch {
      return "Không rõ";
    }
  };

  return (
    <div className="w-full min-h-screen bg-background">
      <Header title="Quản lý nhà bán" subtitle="Xem tất cả hoặc chỉ những nhà bán đang chờ duyệt" />

      <div className="p-6 space-y-6">
        <div className="flex flex-col md:flex-row gap-4 justify-between">
          <div className="relative w-full md:w-96">
            <Search className="absolute left-3 top-3 w-4 h-4 text-muted-foreground" />
            <Input
              placeholder="Tìm tên shop, slug..."
              className="pl-10"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>

          <div className="flex gap-2">
            <Button
              variant={mode === "all" ? "default" : "outline"}
              onClick={() => { setMode("all"); fetchVendors("all"); }}
            >
              Tất cả
            </Button>
            <Button
              variant={mode === "pending" ? "default" : "outline"}
              onClick={() => { setMode("pending"); fetchVendors("pending"); }}
            >
              Chờ duyệt
            </Button>
            <Button variant="outline" onClick={() => fetchVendors(mode)} disabled={loading}>
              <RefreshCw className={`mr-2 h-4 w-4 ${loading ? "animate-spin" : ""}`} />
              Làm mới
            </Button>
          </div>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>
              {mode === "all" ? "Tất cả nhà bán" : "Nhà bán chờ duyệt"} ({filteredVendors.length})
            </CardTitle>
          </CardHeader>
          <CardContent>
            {loading ? (
              <p className="text-center py-8 text-muted-foreground">Đang tải...</p>
            ) : filteredVendors.length === 0 ? (
              <p className="text-center py-8 text-muted-foreground">
                Không có nhà bán nào {mode === "pending" ? "đang chờ duyệt" : ""}
              </p>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead className="border-b">
                    <tr>
                      <th className="text-left py-3 px-4">Logo</th>
                      <th className="text-left py-3 px-4">Tên shop</th>
                      <th className="text-left py-3 px-4">Slug</th>
                      <th className="text-left py-3 px-4">Mô tả</th>
                      <th className="text-left py-3 px-4">Ngày đăng ký</th>
                      <th className="text-left py-3 px-4">Trạng thái</th>
                      <th className="text-left py-3 px-4">Hành động</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredVendors.map((v) => {
                      const cfg = statusConfig[v.status] || statusConfig.PENDING;
                      const Icon = cfg.icon;
                      return (
                        <tr key={v.id} className="border-b hover:bg-muted/50">
                          <td className="py-4 px-4">
                            {v.logoUrl && v.logoUrl !== "test" ? (
                              <img src={v.logoUrl} alt="" className="w-10 h-10 rounded object-cover" />
                            ) : (
                              <div className="w-10 h-10 bg-gray-200 rounded flex items-center justify-center text-xs">?</div>
                            )}
                          </td>
                          <td className="py-4 px-4 font-medium">{v.name}</td>
                          <td className="py-4 px-4 text-muted-foreground">{v.slug}</td>
                          <td className="py-4 px-4 text-muted-foreground max-w-xs truncate">{v.description || "-"}</td>
                          <td className="py-4 px-4 text-muted-foreground">{formatDate(v.joinedAt)}</td>
                          <td className="py-4 px-4">
                            <Badge variant="outline" className={`${cfg.bg} ${cfg.color} border-0`}>
                              <Icon className="w-3 h-3 mr-1" />
                              {cfg.label}
                            </Badge>
                          </td>
                          <td className="py-4 px-4">
                            {v.status === "PENDING" && (
                              <Button
                                size="sm"
                                onClick={() => handleApprove(v.id)}
                                disabled={approvingId === v.id}
                                className="bg-green-600 hover:bg-green-700"
                              >
                                {approvingId === v.id ? "Đang..." : "Phê duyệt"}
                              </Button>
                            )}
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}