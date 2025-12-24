import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { ProductVariant } from '@/interface/product'
import Link from 'next/link'
import React from 'react'

interface ProductCardProps {
  product: ProductVariant
  onView?: (product: ProductVariant) => void // callback khi click View
}

const ProductCard = ({ product, onView }: ProductCardProps) => {
  const skus = product.variants.slice(0, 3).map(v => v.sku)
  const mainImage =
    product.variants[0]?.images.find(img => img.isMain)?.url ||
    product.variants[0]?.images[0]?.url

  return (
    <Card className="w-full shadow-sm border border-slate-200 mb-4">
      <div className="flex items-start gap-4 p-4">
        {/* Ảnh */}
        {mainImage ? (
          <img
            src={mainImage}
            alt={product.name}
            className="w-32 h-32 object-cover rounded-md border"
          />
        ) : (
          <div className="w-32 h-32 flex items-center justify-center bg-gray-100 text-gray-400 rounded-md border">
            No Image
          </div>
        )}

        {/* Nội dung */}
        <div className="flex-1">
          <CardHeader className="p-0 mb-2">
            <CardTitle className="text-lg font-semibold">{product.name}</CardTitle>
            <CardDescription className="text-sm font-sans">{product.description}</CardDescription>
          </CardHeader>

          <CardContent className="p-0 text-sm space-y-1">
            <p><span className="font-semibold">Code:</span> {product.productCode}</p>
            <p><span className="font-semibold">SKU:</span> {skus.join(", ")}</p>
            {product.variants.length > 3 && (
              <p className="text-muted-foreground text-xs">
                +{product.variants.length - 3} more
              </p>
            )}
          </CardContent>

          {/* Nút View */}
          <div className="mt-3">
            <Link href={`/admin/products/${product.id}`}>
            <Button
              onClick={() => onView?.(product)}
              className="px-3 py-1 rounded-md border bg-gray-200 border-gray-300 text-gray-700 hover:bg-gray-100 transition"
              >
              View
            </Button>
              </Link>
          </div>
        </div>
      </div>
    </Card>
  )
}

export default ProductCard
