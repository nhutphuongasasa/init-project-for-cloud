"use client";

import { useState,  useEffect } from "react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import { Search, RefreshCw, CheckCircle, Clock, Ban, Eye } from "lucide-react";
import { Header } from "@/components/custom/header";
import api from "@/lib/axios";
import Link from "next/link";

interface Vendor {
  id: string;
  name: string;
  slug: string;
  description: string;
  logoUrl: string;
  status: string
  joinedAt: string;
}

const vendorHeader: Record<keyof Vendor, string> = {
  id: "ID",
  name: "Name",
  slug: "Slug",
  description: "Description",
  logoUrl: "Logo",
  status: "Status",
  joinedAt: "Joined At",
}

const columns: (keyof Vendor)[] = [
  "id",
  "name",
  "slug",
  "description",
  "logoUrl",
  "status",
  "joinedAt",
]

const statusConfig = {
  ACTIVE: { icon: CheckCircle, color: "text-green-500", bg: "bg-green-500/10", label: "Đã duyệt" },
  PENDING: { icon: Clock, color: "text-yellow-500", bg: "bg-yellow-500/10", label: "Chờ duyệt" },
  SUSPENDED: { icon: Ban, color: "text-red-500", bg: "bg-red-500/10", label: "Bị khóa" },
};

export default function VendorsPage() {
  const [vendors, setVendors] = useState<Vendor[]>([]);
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

      const res = await api.get(url, { withCredentials: true });

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
        logoUrl: v.logoUrl || "chua co logo",
        status: v.status || "PENDING",
        joinedAt: v.joinedAt || v.createdAt || new Date().toISOString(),
      }));

      setVendors(mapped);
    } catch (err: any) {
      console.error("Lỗi fetch:", err);
      alert(err.response?.data?.message || "Lỗi kết nối API");
      setVendors([]);
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (vendorId: string) => {
    if (!confirm("Phê duyệt nhà bán này?")) return;

    try {
      setApprovingId(vendorId);
      await api.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/vendor/admin/approve/${vendorId}`,
        {},
        { withCredentials: true }
      );

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

  const handleDetailVendor = (vendorId: string) => {
    
  }

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
              {mode === "all" ? "Tất cả nhà bán" : "Nhà bán chờ duyệt"} ({vendors.length})
            </CardTitle>
          </CardHeader>
          <CardContent>
            {loading ? (
              <p className="text-center py-8 text-muted-foreground">Đang tải...</p>
            ) : vendors.length === 0 ? (
              <p className="text-center py-8 text-muted-foreground">
                Không có nhà bán nào {mode === "pending" ? "đang chờ duyệt" : ""}
              </p>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                      <thead>
                        <tr className="border-b border-border">
                          {Object.entries(vendorHeader).map(([key, value]) => (
                            <th key={key} className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">
                              {value}
                            </th>
                          ))}
                          <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">
                            Action
                          </th>
                          <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">
                            Detail
                          </th>
                        </tr>
                      </thead>
                  <tbody>
                    {
                      vendors.length === 0 ? (
                        <tr>
                          <td colSpan={Object.keys(vendorHeader).length} className="py-4 px-4 text-center">
                            Không có dữ liệu
                          </td>
                        </tr>
                      ) : (
                        vendors.map((vendor) => (
                          <tr key={vendor.id}>
                            {columns.map((column) => {
                              if (column === "status"){
                                return (
                                  <td key={column} className="py-4 px-4 text-sm">
                                  {vendor.status === "ACTIVE" && (
                                    <span className="px-2 py-1 rounded-full bg-green-100 text-green-600 text-xs font-semibold">
                                      {vendor.status}
                                    </span>
                                  )}
                                  {vendor.status === "PENDING" && (
                                    <span className="px-2 py-1 rounded-full bg-yellow-100 text-yellow-600 text-xs font-semibold">
                                      {vendor.status}
                                    </span>
                                  )}
                                  {vendor.status === "SUSPENDED" && (
                                    <span className="px-2 py-1 rounded-full bg-red-100 text-red-600 text-xs font-semibold">
                                      {vendor.status}
                                    </span>
                                  )}
                                  {vendor.status === "REJECTED" && (
                                    <span className="px-2 py-1 rounded-full bg-rose-100 text-rose-600 text-xs font-semibold">
                                      {vendor.status}
                                    </span>
                                  )}
                                </td>
                                )
                              }
                              if (column === "joinedAt") {
                                return (
                                  <td key={column} className="py-4 px-4 text-sm text-muted-foreground truncate">
                                    {formatDate(vendor.joinedAt)}
                                  </td>
                                );
                              }
                              else{
                                return (
                                  <td key={column} className="py-4 px-4 text-sm font-medium text-muted-foreground truncate">
                                    {vendor[column]}
                                  </td>
                                )
                              }
                            }
                            )}
                            <td className="py-4 px-4 text-sm font-medium text-muted-foreground">
                              {vendor.status === "PENDING" && (
                              <Button
                                className="bg-green-500 text-white hover:bg-green-400"
                                variant="outline"
                                onClick={() => handleApprove(vendor.id)}
                                disabled={approvingId === vendor.id}
                              >
                                {approvingId === vendor.id ? "Đang duyệt..." : "Phê duyệt"}
                              </Button>
                              )}
                            </td>
                            <td>
                              <Link href={`/admin/vendors/${vendor.id}`}>
                                <Button
                                  variant="outline"
                                  className="flex items-center gap-2 border-blue-500 text-blue-600 hover:bg-blue-50 hover:text-blue-700 transition-colors"
                                  onClick={() => handleDetailVendor(vendor.id)}
                                >
                                  <Eye className="h-4 w-4" />
                                  <span>View details</span>
                                </Button>
                              </Link>
                            </td>
                          </tr>
                        ))
                      )
                    }
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