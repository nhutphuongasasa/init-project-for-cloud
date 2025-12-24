"use client";

import React, { useEffect, useState } from "react";
import api from "@/lib/axios";
import { Header } from "@/components/custom/header";
import { Skeleton } from "@/components/ui/skeleton";
import { AlertCircle } from "lucide-react";
import { Card } from "@/components/ui/card";
import { ProductVariantCard } from "@/components/custom/product/product-card";
import { ProductVariant } from "@/interface/product";



export default function ProductDetailPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = React.use(params);
  const [product, setProduct] = useState<ProductVariant | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get(`/api/product/products-query/${id}`)
      .then((res) => {
        setProduct(res.data?.data || res.data)
        console.log(res.data)
      })
      .catch(() => setProduct(null))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) {
    return (
      <>
        <Header title="Chi tiết sản phẩm" subtitle="Đang tải..." />
        <div className="container max-w-4xl mx-auto p-6 space-y-6">
          <Skeleton className="h-12 w-96 rounded-xl" />
          <Card className="p-8">
            <div className="grid md:grid-cols-2 gap-8">
              <Skeleton className="aspect-square rounded-2xl" />
              <div className="space-y-6">
                <Skeleton className="h-10 w-80" />
                <Skeleton className="h-6 w-48" />
                <Skeleton className="h-32 w-full" />
                <Skeleton className="h-12 w-full" />
              </div>
            </div>
          </Card>
        </div>
      </>
    );
  }

  if (!product) {
    return (
      <>
        <Header title="Chi tiết sản phẩm" subtitle="Không tìm thấy" />
        <div className="container max-w-md mx-auto mt-20">
          <Card className="text-center py-16 border-dashed">
            <AlertCircle className="w-20 h-20 text-red-500 mx-auto mb-6" />
            <h2 className="text-3xl font-bold text-gray-800">Không tìm thấy sản phẩm</h2>
            <p className="text-muted-foreground mt-4">ID: <code className="bg-muted px-3 py-1 rounded">{id}</code></p>
          </Card>
        </div>
      </>
    );
  }

  return (
<>
  <Header title="Product" subtitle="product detail" />
  <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 p-4">
    {product.variants.map((variant) => (
      <ProductVariantCard
        key={variant.id}
        productName={product.name}
        productCode={product.productCode}
        productDescription={product.description}
        variant={variant}
      />
    ))}
  </div>
</>

  );
}