"use client"

import { useState } from "react"
// import { Header } from "@/components/header"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Search, Plus, Edit, Trash2 } from "lucide-react"
import { Header } from "@/components/custom/header"

interface VendorProduct {
  id: string
  name: string
  sku: string
  category: string
  status: "ACTIVE" | "DRAFT"
  price: string
  stock: number
  variants: number
}

const vendorProducts: VendorProduct[] = [
  {
    id: "1",
    name: "Premium Laptop Backpack",
    sku: "LP-001",
    category: "Accessories",
    status: "ACTIVE",
    price: "$79.99",
    stock: 245,
    variants: 3,
  },
  {
    id: "2",
    name: "USB-C Hub Pro",
    sku: "HUB-C-002",
    category: "Electronics",
    status: "ACTIVE",
    price: "$49.99",
    stock: 512,
    variants: 2,
  },
  {
    id: "3",
    name: "Mechanical Keyboard",
    sku: "KB-MEC-001",
    category: "Peripherals",
    status: "DRAFT",
    price: "$159.99",
    stock: 0,
    variants: 5,
  },
  {
    id: "4",
    name: "Wireless Mouse",
    sku: "MOUSE-W-001",
    category: "Peripherals",
    status: "ACTIVE",
    price: "$34.99",
    stock: 890,
    variants: 4,
  },
]

export default function VendorProductsPage() {
  const [searchTerm, setSearchTerm] = useState("")
  const [filteredProducts, setFilteredProducts] = useState(vendorProducts)

  const handleSearch = (term: string) => {
    setSearchTerm(term)
    const filtered = vendorProducts.filter(
      (product) =>
        product.name.toLowerCase().includes(term.toLowerCase()) ||
        product.sku.toLowerCase().includes(term.toLowerCase()),
    )
    setFilteredProducts(filtered)
  }

  return (
    <div className="w-full">
      <Header title="Products" subtitle="Manage your product catalog" />

      <div className="p-6 space-y-6">
        {/* Stats */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <Card className="bg-card border-border">
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Total Products</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-foreground">47</div>
            </CardContent>
          </Card>

          <Card className="bg-card border-border">
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Active</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-accent">43</div>
            </CardContent>
          </Card>

          <Card className="bg-card border-border">
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Drafts</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-yellow-500">4</div>
            </CardContent>
          </Card>

          <Card className="bg-card border-border">
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Total Variants</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-primary">156</div>
            </CardContent>
          </Card>
        </div>

        {/* Search and Add */}
        <div className="flex flex-col md:flex-row gap-4 items-start md:items-center justify-between">
          <div className="relative w-full md:w-64">
            <Search className="absolute left-3 top-3 w-4 h-4 text-muted-foreground" />
            <Input
              placeholder="Search your products..."
              className="pl-10 bg-card border-border"
              value={searchTerm}
              onChange={(e) => handleSearch(e.target.value)}
            />
          </div>
          <Button className="bg-primary text-primary-foreground hover:bg-primary/90">
            <Plus size={18} className="mr-2" />
            Create Product
          </Button>
        </div>

        {/* Products Table */}
        <Card className="bg-card border-border">
          <CardHeader>
            <CardTitle>Your Products ({filteredProducts.length})</CardTitle>
            <CardDescription>Manage product details, pricing, and inventory</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-border">
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Product Name</th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">SKU</th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Category</th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Status</th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Price</th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Stock</th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Variants</th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredProducts.map((product) => (
                    <tr key={product.id} className="border-b border-border hover:bg-sidebar/30 transition-colors">
                      <td className="py-4 px-4 text-sm font-medium text-foreground">{product.name}</td>
                      <td className="py-4 px-4 text-sm text-muted-foreground font-mono">{product.sku}</td>
                      <td className="py-4 px-4 text-sm text-muted-foreground">{product.category}</td>
                      <td className="py-4 px-4 text-sm">
                        <Badge
                          variant="outline"
                          className={
                            product.status === "ACTIVE"
                              ? "bg-green-500/10 text-green-500 border-0"
                              : "bg-yellow-500/10 text-yellow-500 border-0"
                          }
                        >
                          {product.status}
                        </Badge>
                      </td>
                      <td className="py-4 px-4 text-sm font-semibold text-accent">{product.price}</td>
                      <td className="py-4 px-4 text-sm text-foreground">{product.stock}</td>
                      <td className="py-4 px-4 text-sm text-primary font-semibold">{product.variants}</td>
                      <td className="py-4 px-4 text-sm">
                        <div className="flex gap-2">
                          <Button variant="ghost" size="icon">
                            <Edit size={16} />
                          </Button>
                          <Button variant="ghost" size="icon">
                            <Trash2 size={16} className="text-destructive" />
                          </Button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
