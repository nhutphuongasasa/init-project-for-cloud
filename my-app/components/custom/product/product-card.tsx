"use client"

import { Card } from "@/components/ui/card"
import { ChevronLeft, ChevronRight } from "lucide-react"
import { useState } from "react"

interface StockSummary {
  warehouseId: string
  warehouseName?: string
  quantityAvailable: number
}

interface ProductImage {
  id: string
  url: string
  isMain: boolean
}

interface ProductVariant {
  id: string
  sku: string
  price: number
  originalPrice: number
  weightGram: number
  images: ProductImage[]
  attributes: Record<string, string>
  inventories: StockSummary[] | null
}

interface ProductVariantCardProps {
  productName: string
  productCode: string
  productDescription: string
  variant: ProductVariant
}

export function ProductVariantCard({ productName, productCode, productDescription, variant }: ProductVariantCardProps) {
  const [selectedImageIndex, setSelectedImageIndex] = useState(0)

  const currentImage =
    variant?.images?.[selectedImageIndex] ||
    variant?.images?.find((img) => img.isMain) ||
    null

  const discount =
    variant.originalPrice > variant.price
      ? Math.round(((variant.originalPrice - variant.price) / variant.originalPrice) * 100)
      : 0

  const handlePrevImage = () => {
    setSelectedImageIndex((prev) => (prev === 0 ? variant.images.length - 1 : prev - 1))
  }

  const handleNextImage = () => {
    setSelectedImageIndex((prev) => (prev === variant.images.length - 1 ? 0 : prev + 1))
  }

  // Warehouse name mapping
  const warehouses: Record<string, string> = {
    "11111111-1111-1111-1111-111111111111": "Hanoi Warehouse",
    "22222222-2222-2222-2222-222222222222": "Saigon Warehouse",
    // Add more warehouses here if needed
  }

  // Helper function to get warehouse name
  const getWarehouseName = (warehouseId: string) => {
    return warehouses[warehouseId] || `Other Warehouse (${warehouseId.slice(0, 8)}...)`
  }

  // Calculate total stock
  const totalStock = variant.inventories?.reduce((sum, inv) => sum + inv.quantityAvailable, 0) ?? 0

  return (
    <Card className="w-full overflow-hidden shadow-lg hover:shadow-xl transition-shadow">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 p-6">
        {/* Image Gallery */}
        <div className="flex flex-col gap-4">
          <div className="relative bg-gradient-to-br from-muted/50 to-muted rounded-lg overflow-hidden aspect-square flex items-center justify-center group">
            <img
              src={currentImage?.url || "/placeholder.svg"}
              alt={productName}
              className="w-full h-full object-cover"
            />
            {variant.images.length > 1 && (
              <>
                <button
                  onClick={handlePrevImage}
                  className="absolute left-2 top-1/2 -translate-y-1/2 bg-background/80 hover:bg-background p-2 rounded-full opacity-0 group-hover:opacity-100 transition-opacity"
                >
                  <ChevronLeft size={20} />
                </button>
                <button
                  onClick={handleNextImage}
                  className="absolute right-2 top-1/2 -translate-y-1/2 bg-background/80 hover:bg-background p-2 rounded-full opacity-0 group-hover:opacity-100 transition-opacity"
                >
                  <ChevronRight size={20} />
                </button>
              </>
            )}
          </div>

          {/* Thumbnail Gallery */}
          <div className="flex gap-x-2">
            {variant?.images?.map((img, idx) => (
              <button
                key={img?.id || idx}
                onClick={() => setSelectedImageIndex(idx)}
                className={`flex-shrink-0 w-16 h-16 rounded-md overflow-hidden border-2 transition-colors ${
                  selectedImageIndex === idx ? "border-primary" : "border-border hover:border-primary/50"
                }`}
              >
                <img
                  src={img?.url || "/placeholder.svg"}
                  alt="variant"
                  className="w-full h-full object-cover"
                />
              </button>
            ))}
          </div>
        </div>

        {/* Product Info */}
        <div className="flex flex-col gap-5">
          <div>
            <div className="flex items-start justify-between gap-2 mb-2">
              <div className="flex-1">
                <h2 className="text-lg font-semibold text-balance">{productName}</h2>
                <p className="text-sm text-muted-foreground">Code: {productCode}</p>
              </div>
            </div>
            <p className="text-sm text-foreground/80 mt-3">{productDescription}</p>
          </div>

          {/* Pricing */}
          <div className="bg-muted/50 rounded-lg p-4">
            <div className="flex flex-col">
              <span className="text-sm font-bold text-primary">
                Price: ${variant.price.toFixed(2)}
              </span>
              {discount > 0 && (
                <span className="text-sm line-through text-muted-foreground">
                  Original: ${variant.originalPrice.toFixed(2)} (-{discount}%)
                </span>
              )}
            </div>
            <p className="text-xs text-muted-foreground mt-2">
              Weight: {variant.weightGram}g
            </p>
          </div>

          {/* Attributes */}
          {Object.keys(variant.attributes).length > 0 && (
            <div>
              <p className="text-sm font-semibold mb-3">Specifications</p>
              <div className="space-y-2">
                {Object.entries(variant.attributes).map(([key, value]) => (
                  <div key={key} className="flex justify-between items-center text-sm">
                    <span className="text-muted-foreground capitalize">{key}:</span>
                    <span className="font-medium">{value}</span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Stock Information with nice warehouse names */}
          {variant.inventories && variant.inventories.length > 0 ? (
            <div className="bg-green-50 dark:bg-green-950/20 rounded-lg p-4 border border-green-200 dark:border-green-800">
              <p className="text-sm font-semibold text-green-800 dark:text-green-300 mb-3">
                Stock Availability (Total: <span className="font-bold">{totalStock}</span> units)
              </p>
              <div className="space-y-2">
                {variant.inventories.map((inv) => (
                  <div key={inv.warehouseId} className="flex justify-between items-center text-sm">
                    <span className="text-muted-foreground font-medium">
                      {getWarehouseName(inv.warehouseId)}
                    </span>
                    <span className="font-bold text-green-700 dark:text-green-400">
                      {inv.quantityAvailable} units
                    </span>
                  </div>
                ))}
              </div>
            </div>
          ) : (
            <div className="bg-amber-50 dark:bg-amber-950/20 rounded-lg p-4 border border-amber-200 dark:border-amber-800">
              <p className="text-sm font-medium text-amber-800 dark:text-amber-300">
                No stock information available
              </p>
            </div>
          )}

          {/* SKU & ID */}
          <div className="pt-4 border-t border-border space-y-2 text-xs text-muted-foreground">
            <div>
              <span className="font-semibold">SKU:</span> {variant.sku || "N/A"}
            </div>
            <div>
              <span className="font-semibold">Variant ID:</span> {variant.id}
            </div>
          </div>
        </div>
      </div>
    </Card>
  )
}