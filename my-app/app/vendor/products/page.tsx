

"use client"

import { useState, useEffect } from "react"
import axios from "axios"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Badge } from "@/components/ui/badge"
import { Textarea } from "@/components/ui/textarea"
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Header } from "@/components/custom/header"
import { Search, Plus, Trash2, X, Eye, Upload, Loader2 } from "lucide-react"
import { toast } from "@/hooks/use-toast"
import { Category, PresignedUrlResponse, ProductCreateRequest } from "@/interface/product"
import { useRouter } from "next/navigation"
import { v4 as uuidv4 } from "uuid" // npm install uuid @types/uuid

interface Product {
  id: string
  name: string
  productCode: string
  slug: string
  description?: string
  status: string
  variants: any[]
  createdAt: string
}

async function getPresignedUrl(fileName: string): Promise<PresignedUrlResponse> {
  const res = await axios.get<PresignedUrlResponse>(
    `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/product/files/presigned-url?fileName=${fileName}`,
    { withCredentials: true }
  )
  return res.data
}

async function getAllCategories(): Promise<Category[]> {
  const res = await axios.get<{ data: Category[] }>(
    `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/product/category/all`,
    { withCredentials: true }
  )
  return res.data.data
}

const MAX_IMAGES_PER_VARIANT = 3

export default function VendorProductsPage() {
  const router = useRouter()

  const [products, setProducts] = useState<Product[]>([])
  const [categories, setCategories] = useState<Category[]>([])
  const [loadingProducts, setLoadingProducts] = useState(true)
  const [loadingCategories, setLoadingCategories] = useState(false)
  const [searchTerm, setSearchTerm] = useState("")
  const [openCreateDialog, setOpenCreateDialog] = useState(false)
  const [isUploading, setIsUploading] = useState(false)

  const [formData, setFormData] = useState<ProductCreateRequest>({
    name: "",
    slug: "",
    description: "",
    categoryId: undefined,
    productVariant: [{
      price: 0,
      originalPrice: 0,
      attributes: {},
      weightGram: 0,
      images: [],
    }],
  })

  useEffect(() => {
    async function fetchMyProducts() {
      try {
        const res = await axios.get<any>(
          `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/product/products-query/my`,
          { withCredentials: true }
        )
        const content = res.data?.data?.content || []
        setProducts(content)
      } catch (error: any) {
        toast({ title: "Lỗi", description: "Không thể tải sản phẩm", variant: "destructive" })
        setProducts([])
      } finally {
        setLoadingProducts(false)
      }
    }
    fetchMyProducts()
  }, [])

  useEffect(() => {
    if (openCreateDialog) {
      async function loadCategories() {
        setLoadingCategories(true)
        try {
          const cats = await getAllCategories()
          const rootActive = cats.filter(cat => cat.isActive)
          setCategories(rootActive)
        } catch (error) {
          toast({ title: "Lỗi", description: "Không thể tải danh mục", variant: "destructive" })
          setCategories([])
        } finally {
          setLoadingCategories(false)
        }
      }
      loadCategories()
    } else {
      setCategories([])
    }
  }, [openCreateDialog])

  const filteredProducts = products.filter(p =>
    p.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    p.productCode.toLowerCase().includes(searchTerm.toLowerCase())
  )

  // Upload ảnh - sửa lỗi nhân đôi bằng immutable update
  const handleImageUpload = async (files: FileList | null, variantIndex: number) => {
    if (!files || files.length === 0) return

    const currentImages = formData.productVariant[variantIndex].images ?? []
    const slotsLeft = MAX_IMAGES_PER_VARIANT - currentImages.length

    if (slotsLeft <= 0) {
      toast({ title: "Giới hạn", description: `Tối đa ${MAX_IMAGES_PER_VARIANT} ảnh mỗi variant`, variant: "destructive" })
      return
    }

    const filesToUpload = Array.from(files).slice(0, slotsLeft)

    setIsUploading(true)

    try {
      const uploadPromises = filesToUpload.map(async (file) => {
        const uniqueId = uuidv4()
        const fileExtension = file.name.split('.').pop() || 'jpg'
        const fileName = `${uniqueId}.${fileExtension}`

        const presigned = await getPresignedUrl(fileName)
        await axios.put(presigned.url, file, {
          headers: { "Content-Type": file.type || "application/octet-stream" },
        })

        const publicUrl = presigned.url.split("?")[0]
        return { url: publicUrl, isMain: false }
      })

      const newImages = await Promise.all(uploadPromises)

      // Immutable update - tạo variant mới hoàn toàn
      setFormData(prev => ({
        ...prev,
        productVariant: prev.productVariant.map((variant, idx) => {
          if (idx === variantIndex) {
            const combinedImages = [...(variant.images ?? []), ...newImages]
            const finalImages = combinedImages.map((img, i) => ({
              ...img,
              isMain: i === 0
            }))
            return {
              ...variant,
              images: finalImages
            }
          }
          return variant
        })
      }))

      toast({ title: "Thành công", description: `Đã upload ${newImages.length} ảnh` })
    } catch (error) {
      toast({ title: "Lỗi", description: "Upload thất bại", variant: "destructive" })
    } finally {
      setIsUploading(false)
    }
  }

  // Xóa ảnh - immutable update
  const removeImage = (variantIndex: number, imageUrl: string) => {
    setFormData(prev => ({
      ...prev,
      productVariant: prev.productVariant.map((variant, idx) => {
        if (idx === variantIndex) {
          const filtered = (variant.images ?? []).filter(img => img.url !== imageUrl)
          const finalImages = filtered.map((img, i) => ({
            ...img,
            isMain: i === 0
          }))
          return {
            ...variant,
            images: finalImages
          }
        }
        return variant
      })
    }))
  }

  const addVariant = () => {
    setFormData(prev => ({
      ...prev,
      productVariant: [...prev.productVariant, {
        price: 0,
        originalPrice: 0,
        attributes: {},
        weightGram: 0,
        images: [],
      }]
    }))
  }

  const addAttribute = (variantIndex: number) => {
    setFormData(prev => ({
      ...prev,
      productVariant: prev.productVariant.map((v, i) => 
        i === variantIndex 
          ? { ...v, attributes: { ...v.attributes, "": "" } }
          : v
      )
    }))
  }

  const updateAttribute = (variantIndex: number, oldKey: string, newKey: string, value: string) => {
    setFormData(prev => ({
      ...prev,
      productVariant: prev.productVariant.map((v, i) => {
        if (i === variantIndex) {
          const attrs = { ...v.attributes }
          if (oldKey !== newKey) delete attrs[oldKey]
          if (newKey.trim()) attrs[newKey.trim()] = value.trim()
          return { ...v, attributes: attrs }
        }
        return v
      })
    }))
  }

  const removeAttribute = (variantIndex: number, key: string) => {
    setFormData(prev => ({
      ...prev,
      productVariant: prev.productVariant.map((v, i) => {
        if (i === variantIndex) {
          const attrs = { ...v.attributes }
          delete attrs[key]
          return { ...v, attributes: attrs }
        }
        return v
      })
    }))
  }

  const handleCreateProduct = async () => {
    const invalidVariant = formData.productVariant.some(v =>
      v.price <= 0 || v.originalPrice <= 0 || v.weightGram <= 0
    )
    if (!formData.name.trim() || !formData.slug.trim() || invalidVariant) {
      toast({ title: "Lỗi", description: "Vui lòng điền đầy đủ thông tin bắt buộc", variant: "destructive" })
      return
    }

    try {
      await axios.post(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/product/products-command`, formData, {
        withCredentials: true,
      })
      toast({ title: "Thành công", description: "Tạo sản phẩm thành công!" })
      setOpenCreateDialog(false)
      setFormData({
        name: "",
        slug: "",
        description: "",
        categoryId: undefined,
        productVariant: [{ price: 0, originalPrice: 0, attributes: {}, weightGram: 0, images: [] }],
      })
      window.location.reload()
    } catch (error: any) {
      toast({
        title: "Lỗi",
        description: error.response?.data?.message || "Tạo sản phẩm thất bại",
        variant: "destructive",
      })
    }
  }

  if (loadingProducts) {
    return <div className="p-6 text-center text-muted-foreground">Đang tải sản phẩm...</div>
  }

  return (
    <div className="w-full">
      <Header title="Sản phẩm" subtitle="Quản lý danh mục sản phẩm của bạn" />

      <div className="p-6 space-y-6">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <Card>
            <CardHeader className="pb-2"><CardTitle className="text-sm font-medium text-muted-foreground">Tổng sản phẩm</CardTitle></CardHeader>
            <CardContent><div className="text-2xl font-bold">{products.length}</div></CardContent>
          </Card>
          <Card>
            <CardHeader className="pb-2"><CardTitle className="text-sm font-medium text-muted-foreground">Đang hoạt động</CardTitle></CardHeader>
            <CardContent><div className="text-2xl font-bold text-green-600">
              {products.filter(p => p.status === "ACTIVE").length}
            </div></CardContent>
          </Card>
        </div>

        <div className="flex flex-col md:flex-row justify-between gap-4">
          <div className="relative w-full md:w-96">
            <Search className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
            <Input
              placeholder="Tìm kiếm theo tên hoặc mã sản phẩm..."
              className="pl-10"
              value={searchTerm}
              onChange={e => setSearchTerm(e.target.value)}
            />
          </div>

          <Dialog open={openCreateDialog} onOpenChange={setOpenCreateDialog}>
            <DialogTrigger asChild>
              <Button><Plus className="mr-2 h-4 w-4" /> Tạo sản phẩm mới</Button>
            </DialogTrigger>

            <DialogContent className="max-w-5xl max-h-[90vh] overflow-y-auto">
              <DialogHeader>
                <DialogTitle>Tạo sản phẩm mới</DialogTitle>
                <DialogDescription>Điền đầy đủ thông tin sản phẩm và ít nhất một variant</DialogDescription>
              </DialogHeader>

              <div className="space-y-8 py-4">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <Label>Tên sản phẩm <span className="text-red-500">*</span></Label>
                    <Input value={formData.name} onChange={e => setFormData({ ...formData, name: e.target.value })} placeholder="Áo thun nam" />
                  </div>
                  <div>
                    <Label>Slug <span className="text-red-500">*</span></Label>
                    <Input value={formData.slug} onChange={e => setFormData({ ...formData, slug: e.target.value })} placeholder="ao-thun-nam" />
                  </div>
                </div>

                <div>
                  <Label>Mô tả</Label>
                  <Textarea value={formData.description || ""} onChange={e => setFormData({ ...formData, description: e.target.value })} rows={3} />
                </div>

                <div>
                  <Label>Danh mục</Label>
                  {loadingCategories ? (
                    <div className="py-2 text-sm text-muted-foreground">Đang tải danh mục...</div>
                  ) : categories.length === 0 ? (
                    <div className="py-2 text-sm text-muted-foreground">Không có danh mục nào</div>
                  ) : (
                    <Select value={formData.categoryId} onValueChange={value => setFormData({ ...formData, categoryId: value })}>
                      <SelectTrigger><SelectValue placeholder="Chọn danh mục" /></SelectTrigger>
                      <SelectContent>
                        {categories.map(cat => (
                          <SelectItem key={cat.id} value={cat.id}>
                            <div className="flex items-center gap-3">
                              {cat.iconUrl && <img src={cat.iconUrl} alt={cat.name} className="w-5 h-5 object-contain" />}
                              <span>{cat.name}</span>
                            </div>
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  )}
                </div>

                <div className="space-y-8">
                  <div className="flex justify-between items-center">
                    <Label className="text-lg font-medium">Variants ({formData.productVariant.length})</Label>
                    <Button variant="outline" size="sm" onClick={addVariant}>Thêm variant</Button>
                  </div>

                  {formData.productVariant.map((variant, vIdx) => (
                    <Card key={vIdx} className="p-6 border">
                      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
                        <div>
                          <Label>Giá bán *</Label>
                          <Input type="number" value={variant.price} onChange={e => {
                            setFormData(prev => ({
                              ...prev,
                              productVariant: prev.productVariant.map((v, i) => i === vIdx ? { ...v, price: Number(e.target.value) } : v)
                            }))
                          }} />
                        </div>
                        <div>
                          <Label>Giá gốc *</Label>
                          <Input type="number" value={variant.originalPrice} onChange={e => {
                            setFormData(prev => ({
                              ...prev,
                              productVariant: prev.productVariant.map((v, i) => i === vIdx ? { ...v, originalPrice: Number(e.target.value) } : v)
                            }))
                          }} />
                        </div>
                        <div>
                          <Label>Trọng lượng (gram) *</Label>
                          <Input type="number" value={variant.weightGram} onChange={e => {
                            setFormData(prev => ({
                              ...prev,
                              productVariant: prev.productVariant.map((v, i) => i === vIdx ? { ...v, weightGram: Number(e.target.value) } : v)
                            }))
                          }} />
                        </div>
                      </div>

                      <div className="mb-6">
                        <div className="flex justify-between items-center mb-3">
                          <Label>Thuộc tính variant</Label>
                          <Button variant="outline" size="sm" onClick={() => addAttribute(vIdx)}>
                            <Plus className="h-4 w-4 mr-1" /> Thêm thuộc tính
                          </Button>
                        </div>

                        {Object.keys(variant.attributes).length === 0 ? (
                          <p className="text-sm text-muted-foreground">Chưa có thuộc tính</p>
                        ) : (
                          <div className="space-y-3">
                            {Object.entries(variant.attributes).map(([key, value], aIdx) => (
                              <div key={aIdx} className="flex items-center gap-3">
                                <Input
                                  placeholder="Tên thuộc tính (Color, Size...)"
                                  value={key}
                                  onChange={e => updateAttribute(vIdx, key, e.target.value, value)}
                                  className="w-48"
                                />
                                <span className="text-muted-foreground">→</span>
                                <Input
                                  placeholder="Giá trị (Red, M...)"
                                  value={value}
                                  onChange={e => updateAttribute(vIdx, key, key, e.target.value)}
                                  className="flex-1"
                                />
                                <Button variant="ghost" size="icon" onClick={() => removeAttribute(vIdx, key)}>
                                  <X className="h-4 w-4" />
                                </Button>
                              </div>
                            ))}
                          </div>
                        )}
                      </div>

                      <div>
                        <div className="flex items-center justify-between mb-2">
                          <Label>Ảnh variant (tối đa {MAX_IMAGES_PER_VARIANT} ảnh)</Label>
                          <span className="text-sm text-muted-foreground">
                            {(variant.images?.length || 0)}/{MAX_IMAGES_PER_VARIANT}
                          </span>
                        </div>

                        <Input
                          type="file"
                          accept="image/*"
                          multiple
                          className="cursor-pointer"
                          onChange={(e) => handleImageUpload(e.target.files, vIdx)}
                          disabled={isUploading || (variant.images?.length || 0) >= MAX_IMAGES_PER_VARIANT}
                        />

                        <div
                          className="mt-4 border-2 border-dashed rounded-lg p-10 text-center cursor-pointer hover:border-primary/50 transition"
                          onDragOver={(e) => e.preventDefault()}
                          onDrop={(e) => {
                            e.preventDefault()
                            handleImageUpload(e.dataTransfer.files, vIdx)
                          }}
                        >
                          {isUploading ? (
                            <div className="flex flex-col items-center gap-3">
                              <Loader2 className="h-10 w-10 animate-spin text-primary" />
                              <p className="text-muted-foreground">Đang upload ảnh...</p>
                            </div>
                          ) : (
                            <div className="flex flex-col items-center gap-2 text-muted-foreground">
                              <Upload className="h-10 w-10" />
                              <p>Kéo thả hoặc click chọn file</p>
                            </div>
                          )}
                        </div>

                        {(variant.images ?? []).length > 0 && (
                          <div className="mt-6 grid grid-cols-3 sm:grid-cols-4 md:grid-cols-6 gap-4">
                            {(variant.images ?? []).map((img) => (
                              <div key={img.url} className="relative group">
                                <img
                                  src={img.url}
                                  alt="preview"
                                  className="w-full h-40 object-cover rounded-lg border"
                                />
                                {img.isMain && (
                                  <Badge className="absolute top-2 left-2 text-xs">Ảnh chính</Badge>
                                )}
                                <Button
                                  variant="destructive"
                                  size="icon"
                                  className="absolute top-2 right-2 opacity-0 group-hover:opacity-100 transition h-8 w-8"
                                  onClick={() => removeImage(vIdx, img.url)}
                                  disabled={isUploading}
                                >
                                  <Trash2 className="h-4 w-4" />
                                </Button>
                              </div>
                            ))}
                          </div>
                        )}
                      </div>
                    </Card>
                  ))}
                </div>
              </div>

              <DialogFooter>
                <Button variant="outline" onClick={() => setOpenCreateDialog(false)}>Hủy</Button>
                <Button onClick={handleCreateProduct} disabled={isUploading}>
                  {isUploading ? (
                    <>
                      <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                      Đang xử lý...
                    </>
                  ) : (
                    "Tạo sản phẩm"
                  )}
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>Danh sách sản phẩm ({filteredProducts.length})</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead className="border-b">
                  <tr>
                    <th className="text-left py-3 px-4 font-medium">Tên sản phẩm</th>
                    <th className="text-left py-3 px-4 font-medium">Mã SP</th>
                    <th className="text-left py-3 px-4 font-medium">Trạng thái</th>
                    <th className="text-center py-3 px-4 font-medium">Variants</th>
                    <th className="text-right py-3 px-4 font-medium">Hành động</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredProducts.length === 0 ? (
                    <tr><td colSpan={5} className="text-center py-8 text-muted-foreground">Không có sản phẩm nào</td></tr>
                  ) : (
                    filteredProducts.map(p => (
                      <tr key={p.id} className="border-b hover:bg-muted/50">
                        <td className="py-4 px-4 font-medium">{p.name}</td>
                        <td className="py-4 px-4 font-mono text-xs">{p.productCode}</td>
                        <td className="py-4 px-4">
                          <Badge variant={p.status === "ACTIVE" ? "default" : "secondary"}>{p.status}</Badge>
                        </td>
                        <td className="py-4 px-4 text-center">{p.variants.length}</td>
                        <td className="py-4 px-4 text-right">
                          <div className="flex justify-end gap-2">
                            <Button variant="ghost" size="icon" onClick={() => router.push("/vendor/products/" + p.id)}><Eye className="h-4 w-4" /></Button>
                            <Button variant="ghost" size="icon"><Trash2 className="h-4 w-4 text-destructive" /></Button>
                          </div>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}