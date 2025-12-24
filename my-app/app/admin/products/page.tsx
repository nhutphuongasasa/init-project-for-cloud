"use client"

import { useState, useEffect } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { Label } from "@/components/ui/label"
import { Search, Plus, MoreHorizontal, Eye, RefreshCw, Trash2, Edit2, Columns } from "lucide-react"
import { Header } from "@/components/custom/header"
import api from "@/lib/axios"
import Link from "next/link"
import { Category, columns, Product, productHeaders, ProductStatusConfig } from "@/interface/product"



export default function ProductsPage() {
  const [activeTab, setActiveTab] = useState("products")
  const [products, setProducts] = useState<Product[]>([])
  const [filteredProducts, setFilteredProducts] = useState<Product[]>([])
  const [categories, setCategories] = useState<Category[]>([])
  const [searchTerm, setSearchTerm] = useState("")
  const [loadingProducts, setLoadingProducts] = useState(true)
  const [loadingCategories, setLoadingCategories] = useState(true)
  const [openAddCategory, setOpenAddCategory] = useState(false)
  const [newCategory, setNewCategory] = useState({ name: "", slug: "", iconUrl: "" })

  // Load sản phẩm
  const fetchProducts = async () => {
    try {
      setLoadingProducts(true)
      const res = await api.get("/api/product/products-query")
      const content = res.data?.data?.content || []
      const mapped: Product[] = content.map((p: any) => ({
        id: p.id,
        vendorId: p.vendorId,
        name: p.name || "Chưa đặt tên",
        slug: p.slug || "",
        productCode: p.productCode || "-",
        description: p.description || "-",
        categoryId: p.categoryId || "-",
        status: p.status || "DRAFT",
        createdAt: p.createdAt || "-",
      }))
      setProducts(mapped)
      // setFilteredProducts(mapped)
    } catch (err) {
      console.error("Lỗi load sản phẩm:", err)
      setProducts([])
      // setFilteredProducts([])
    } finally {
      setLoadingProducts(false)
    }
  }

  // Load danh mục
  const fetchCategories = async () => {
    try {
      setLoadingCategories(true)
      const res = await api.get("/api/product/category/all")
      const list = res.data?.data || []
      setCategories(list)
    } catch (err) {
      console.error("Lỗi load danh mục:", err)
      setCategories([])
    } finally {
      setLoadingCategories(false)
    }
  }

  useEffect(() => {
    if (activeTab === "products") fetchProducts()
    if (activeTab === "categories") fetchCategories()
  }, [activeTab])

  // Tìm kiếm sản phẩm
  useEffect(() => {
    const term = searchTerm.toLowerCase()
    const filtered = products.filter(p =>
      p.name.toLowerCase().includes(term) ||
      p.productCode.toLowerCase().includes(term) ||
      p.categoryId.toLowerCase().includes(term)
    )
    setFilteredProducts(filtered)
  }, [searchTerm, products])

  // Tạo danh mục
  const handleCreateCategory = async () => {
    try {
      const slug = newCategory.slug || newCategory.name.toLowerCase().replace(/\s+/g, "-")
      await api.post("/api/product/category", {
        name: newCategory.name,
        slug,
        iconUrl: newCategory.iconUrl || "https://via.placeholder.com/64",
        parentId: null
      })
      setOpenAddCategory(false)
      setNewCategory({ name: "", slug: "", iconUrl: "" })
      fetchCategories()
    } catch (err) {
      console.error("Tạo danh mục thất bại:", err)
    }
  }

  // Xóa danh mục
  const handleDeleteCategory = async (id: string) => {
    if (!confirm("Xóa danh mục này?")) return
    try {
      await api.delete(`/api/product/category/${id}`)
      fetchCategories()
    } catch (err) {
      console.error("Xóa thất bại:", err)
    }
  }

  //get detail product
  const handleDetailProduct = (productId: string) => {
    
  }

  return (
    <div className="w-full">
      <Header title="Quản lý sản phẩm & danh mục" subtitle="Admin có toàn quyền quản lý sản phẩm và danh mục"/>

      <div className="p-6">
        <Tabs value={activeTab} onValueChange={setActiveTab} className="space-y-6">
          <TabsList className="grid w-full max-w-md grid-cols-2">
            <TabsTrigger value="products">Sản phẩm</TabsTrigger>
            <TabsTrigger value="categories">Danh mục</TabsTrigger>
          </TabsList>

          {/* TAB SẢN PHẨM    phai goi api de lay*/}
          <TabsContent value="products" className="space-y-6">
            {/* Stats */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <Card className="bg-card border-border">
                <CardHeader className="pb-2">
                  <CardTitle className="text-sm font-medium text-muted-foreground">Tổng sản phẩm</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold text-foreground">
                    {loadingProducts ? "..." : products.length.toLocaleString()}
                  </div>
                </CardContent>
              </Card>

              <Card className="bg-card border-border">
                <CardHeader className="pb-2">
                  <CardTitle className="text-sm font-medium text-muted-foreground">Đang hoạt động</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold text-green-500">
                    {loadingProducts ? "..." : products.filter(p => p.status === "ACTIVE").length}
                  </div>
                </CardContent>
              </Card>

              <Card className="bg-card border-border">
                <CardHeader className="pb-2">
                  <CardTitle className="text-sm font-medium text-muted-foreground">Cảnh báo hết hàng</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold text-orange-500">
                    {loadingProducts ? "..." : products.filter(p => p.status === "DRAFT").length}
                  </div>
                </CardContent>
              </Card>
            </div>

            {/* Search + Refresh */}
            <div className="flex flex-col md:flex-row gap-4 items-start md:items-center justify-between">
              <div className="relative w-full md:w-96">
                <Search className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
                <Input
                  placeholder="Tìm tên, SKU, nhà bán, danh mục..."
                  className="pl-10 bg-card border-border"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </div>
              <div className="flex gap-2">
                <Button variant="outline" onClick={fetchProducts} disabled={loadingProducts}>
                  <RefreshCw className={`mr-2 h-4 w-4 ${loadingProducts ? "animate-spin" : ""}`} />
                  Làm mới
                </Button>
                {/* <Button>
                  <Plus className="mr-2 h-4 w-4" />
                  Thêm sản phẩm
                </Button> */}
              </div>
            </div>

            {/* Bảng sản phẩm */}
            <Card className="bg-card border-border">
              <CardHeader>
                <CardTitle>Sản phẩm ({products.length})</CardTitle>
                <CardDescription>Admin có toàn quyền quản lý tất cả sản phẩm</CardDescription>
              </CardHeader>
              <CardContent>
                {loadingProducts ? (
                  <div className="text-center py-16 text-muted-foreground">Đang tải sản phẩm...</div>
                ) : products.length === 0 ? (
                  <div className="text-center py-16 text-muted-foreground">Không tìm thấy sản phẩm nào</div>
                ) : (
                  <div className="overflow-x-auto">
                    <table className="w-full">
                      <thead>
                        <tr className="border-b border-border">
                          {Object.entries(productHeaders).map(([key, value]) => (
                            <th key={key} className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">
                              {value}
                            </th>
                          ))}
                          <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">
                            Action
                          </th>
                        </tr>
                      </thead>
                      <tbody>
                        {products.map((product) => (
                          <tr key={product.id}>
                            {columns.map((col) => {
                              if(col === "status"){
                                const config = ProductStatusConfig[product.status]
                                return (
                                  <td key={col} className="py-3 px-4 text-sm text-muted-foreground truncate">
                                    {config ? (
                                      <span
                                        className={`px-2 py-1 rounded-full ${config.bg} ${config.text} text-xs font-semibold`}
                                      >
                                        {config.label}
                                      </span>
                                    ) : (
                                      <span className="px-2 py-1 rounded-full bg-slate-200 text-slate-600 text-xs font-semibold">
                                        {product.status}
                                      </span>
                                    )}
                                  </td>
                                )
                              }else{
                                return(
                                  <td key={col} className="py-3 px-4 test-sm text-muted-foreground truncate">{product[col]}</td>
                                )
                              }
                            })}
                            <td className="py-3 px-4 text-sm text-center">
                              <Link href={`/admin/products/${product.id}`}>
                                  <Button
                                    variant="outline"
                                    className="flex items-center gap-2 border-blue-500 text-blue-600 hover:bg-blue-50 hover:text-blue-700 transition-colors"
                                    // onClick={() => handleDetailProduct(product.id)}
                                  >
                                  <Eye className="h-4 w-4" />
                                  <span>View details</span>
                                </Button>
                              </Link>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </CardContent>
            </Card>
          </TabsContent>

          {/* TAB DANH MỤC */}
          <TabsContent value="categories" className="space-y-6">
            <div className="flex justify-between items-center">
              <h2 className="text-2xl font-bold">Danh mục sản phẩm</h2>
              <Dialog open={openAddCategory} onOpenChange={setOpenAddCategory}>
                <DialogTrigger asChild>
                  <Button>
                    <Plus className="mr-2 h-4 w-4" />
                    Add new category
                  </Button>
                </DialogTrigger>
                <DialogContent>
                  <DialogHeader>
                    <DialogTitle>Tạo danh mục mới</DialogTitle>
                  </DialogHeader>
                  <div className="space-y-4">
                    <div>
                      <Label>Tên danh mục</Label>
                      <Input
                        placeholder="Ví dụ: Quần áo"
                        value={newCategory.name}
                        onChange={(e) => setNewCategory({ ...newCategory, name: e.target.value })}
                      />
                    </div>
                    <div>
                      <Label>Slug (tự động nếu để trống)</Label>
                      <Input
                        placeholder="quan-ao"
                        value={newCategory.slug}
                        onChange={(e) => setNewCategory({ ...newCategory, slug: e.target.value })}
                      />
                    </div>
                    <div>
                      <Label>Icon URL (tùy chọn)</Label>
                      <Input
                        placeholder="https://example.com/icon.png"
                        value={newCategory.iconUrl}
                        onChange={(e) => setNewCategory({ ...newCategory, iconUrl: e.target.value })}
                      />
                    </div>
                    <Button onClick={handleCreateCategory} className="w-full">
                      Tạo danh mục
                    </Button>
                  </div>
                </DialogContent>
              </Dialog>
            </div>

            {loadingCategories ? (
              <div className="text-center py-12">Đang tải danh mục...</div>
            ) : categories.length === 0 ? (
              <div className="text-center py-12 text-muted-foreground">Chưa có danh mục nào</div>
            ) : (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
                {categories.map((cat) => (
                  <Card key={cat.id} className="hover:shadow-lg transition-shadow">
                    <CardHeader className="pb-3">
                      <div className="flex items-center gap-3">
                        {cat.iconUrl && cat.iconUrl !== "phuong" ? (
                          <img src={cat.iconUrl} alt={cat.name} className="w-12 h-12 rounded-lg object-cover" />
                        ) : (
                          <div className="w-12 h-12 bg-gray-200 rounded-lg flex items-center justify-center flex text-xs font-medium">
                            {cat.name.charAt(0).toUpperCase()}
                          </div>
                        )}
                        <div>
                          <CardTitle className="text-lg">{cat.name}</CardTitle>
                          <p className="text-sm text-muted-foreground">{cat.slug}</p>
                        </div>
                      </div>
                    </CardHeader>
                    <CardContent>
                      <div className="flex justify-between items-center">
                        <Badge variant={cat.isActive ? "default" : "secondary"}>
                          {cat.isActive ? "Hoạt động" : "Tắt"}
                        </Badge>
                        <div className="flex gap-1">
                          <Button variant="ghost" size="icon" className="h-8 w-8">
                            <Edit2 className="h-4 w-4" />
                          </Button>
                          <Button
                            variant="ghost"
                            size="icon"
                            className="h-8 w-8 text-destructive"
                            onClick={() => handleDeleteCategory(cat.id)}
                          >
                            <Trash2 className="h-4 w-4" />
                          </Button>
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            )}
          </TabsContent>
        </Tabs>
      </div>
    </div>
  )
}