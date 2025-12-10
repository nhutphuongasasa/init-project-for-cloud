"use client"

import { useState } from "react"
// import { Header } from "@/components/header"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Search, Plus, MoreHorizontal, Eye } from "lucide-react"
import { Header } from "@/components/custom/header"

interface Product {
  id: string
  name: string
  sku: string
  vendor: string
  category: string
  status: "ACTIVE" | "DRAFT" | "INACTIVE"
  price: string
  stock: number
  sales: number
}

const products: Product[] = [
  {
    id: "1",
    name: "Wireless Earbuds Pro",
    sku: "WEB-001",
    vendor: "TechStore Vietnam",
    category: "Electronics",
    status: "ACTIVE",
    price: "$89.99",
    stock: 342,
    sales: 1240,
  },
  {
    id: "2",
    name: "Premium Leather Wallet",
    sku: "FH-202",
    vendor: "Fashion Hub Co.",
    category: "Accessories",
    status: "ACTIVE",
    price: "$45.50",
    stock: 156,
    sales: 456,
  },
  {
    id: "3",
    name: "Smart Watch Ultra",
    sku: "EP-445",
    vendor: "Electronics Plus",
    category: "Electronics",
    status: "ACTIVE",
    price: "$299.99",
    stock: 89,
    sales: 234,
  },
  {
    id: "4",
    name: "Organic Coffee Blend",
    sku: "HE-100",
    vendor: "Home Essentials",
    category: "Food & Beverage",
    status: "DRAFT",
    price: "$18.99",
    stock: 0,
    sales: 0,
  },
  {
    id: "5",
    name: "Memory Foam Pillow Set",
    sku: "HE-250",
    vendor: "Home Essentials",
    category: "Home",
    status: "ACTIVE",
    price: "$129.99",
    stock: 234,
    sales: 678,
  },
]

const statusConfig = {
  ACTIVE: { bg: "bg-green-500/10", text: "text-green-500" },
  DRAFT: { bg: "bg-yellow-500/10", text: "text-yellow-500" },
  INACTIVE: { bg: "bg-gray-500/10", text: "text-gray-500" },
}

export default function ProductsPage() {
  const [searchTerm, setSearchTerm] = useState("")
  const [filteredProducts, setFilteredProducts] = useState(products)

  const handleSearch = (term: string) => {
    setSearchTerm(term)
    const filtered = products.filter(
      (product) =>
        product.name.toLowerCase().includes(term.toLowerCase()) ||
        product.sku.toLowerCase().includes(term.toLowerCase()) ||
        product.vendor.toLowerCase().includes(term.toLowerCase()),
    )
    setFilteredProducts(filtered)
  }

  const lowStockCount = products.filter((p) => p.stock < 100 && p.stock > 0).length

  return (
    <div className="w-full">
      <Header title="Products" subtitle="Manage all products across vendors" />

      <div className="p-6 space-y-6">
        {/* Stats */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <Card className="bg-card border-border">
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Total Products</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-foreground">5,240</div>
            </CardContent>
          </Card>

          <Card className="bg-card border-border">
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Active Products</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-accent">4,950</div>
            </CardContent>
          </Card>

          <Card className="bg-card border-border">
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Low Stock Alert</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-orange-500">{lowStockCount}</div>
            </CardContent>
          </Card>
        </div>

        {/* Search and Add */}
        <div className="flex flex-col md:flex-row gap-4 items-start md:items-center justify-between">
          <div className="relative w-full md:w-64">
            <Search className="absolute left-3 top-3 w-4 h-4 text-muted-foreground" />
            <Input
              placeholder="Search products..."
              className="pl-10 bg-card border-border"
              value={searchTerm}
              onChange={(e) => handleSearch(e.target.value)}
            />
          </div>
          <Button className="bg-primary text-primary-foreground hover:bg-primary/90">
            <Plus size={18} className="mr-2" />
            Add Product
          </Button>
        </div>

        {/* Products Table */}
        <Card className="bg-card border-border">
          <CardHeader>
            <CardTitle>Product Catalog ({filteredProducts.length})</CardTitle>
            <CardDescription>Complete product inventory across all vendors</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-border">
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Product Name</th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">SKU</th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Vendor</th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Category</th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Status</th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Price</th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Stock</th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Sales</th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredProducts.map((product) => (
                    <tr key={product.id} className="border-b border-border hover:bg-sidebar/30 transition-colors">
                      <td className="py-4 px-4 text-sm font-medium text-foreground">{product.name}</td>
                      <td className="py-4 px-4 text-sm text-muted-foreground font-mono">{product.sku}</td>
                      <td className="py-4 px-4 text-sm text-muted-foreground">{product.vendor}</td>
                      <td className="py-4 px-4 text-sm text-muted-foreground">{product.category}</td>
                      <td className="py-4 px-4 text-sm">
                        <Badge
                          variant="outline"
                          className={`${statusConfig[product.status].bg} ${statusConfig[product.status].text} border-0`}
                        >
                          {product.status}
                        </Badge>
                      </td>
                      <td className="py-4 px-4 text-sm font-semibold text-accent">{product.price}</td>
                      <td className="py-4 px-4 text-sm">
                        <span className={product.stock < 100 ? "text-orange-500 font-semibold" : "text-foreground"}>
                          {product.stock}
                        </span>
                      </td>
                      <td className="py-4 px-4 text-sm text-foreground">{product.sales}</td>
                      <td className="py-4 px-4 text-sm">
                        <div className="flex gap-2">
                          <Button variant="ghost" size="icon">
                            <Eye size={16} />
                          </Button>
                          <Button variant="ghost" size="icon">
                            <MoreHorizontal size={16} />
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
