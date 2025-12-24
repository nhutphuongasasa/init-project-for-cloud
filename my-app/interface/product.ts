export type ProductStatus = "ACTIVE" | "DRAFT" | "INACTIVE" | "BANNED" | "SUSPENDED"


export interface Product {
  id: string
  vendorId: string
  name: string
  slug: string
  productCode: string
  description: string
  categoryId: string
  status: ProductStatus
  createdAt: string
}

export interface ProductVariant {
  id: string;
  vendorId: string;
  name: string;
  description: string;
  productCode: string;
  variants: Array<{
    id: string;
    sku: string;
    price: number;
    originalPrice: number;
    weightGram: number;
    images: Array<{ id: string; url: string; isMain: boolean }>;
    inventories: Array<{ warehouseId: string; quantityAvailable: number }>;
    attributes: Record<string, string>;
  }>;
}

export const productHeaders: Record<keyof Product, string> = {
  id: "ID",
  vendorId: "vendorId",
  name: "ProductName",
  slug: "Slug",
  productCode: "ProductCode",
  description: "Description",
  categoryId: "CategoryId",
  status: "Status",
  createdAt: "CreatedAt",
}

export const columns: (keyof Product)[] = [
  "id",
  "vendorId",
  "name",
  "slug",
  "productCode",
  "description",
  "categoryId",
  "status",
  "createdAt",
]

export interface Category {
  id: string
  name: string
  slug: string
  iconUrl: string
  isActive: boolean
}

export const ProductStatusConfig: Record<ProductStatus, { 
    bg: string; 
    text: string; 
    label: string 
}> = {
  ACTIVE:   { bg: "bg-green-500/10", text: "text-green-500", label: "ACTIVE" },
  DRAFT:    { bg: "bg-yellow-500/10", text: "text-yellow-500", label: "DRAFT" },
  INACTIVE: { bg: "bg-gray-500/10", text: "text-gray-500", label: "INACTIVE" },
  BANNED:   { bg: "bg-red-500/10", text: "text-red-500", label: "BANNED" },
  SUSPENDED:{ bg: "bg-rose-500/10", text: "text-rose-500", label: "SUSPENDED" },
}


export interface ProductImageRequest {
  url: string
  isMain: boolean
}

export interface ProductVariantRequest {
  price: number
  originalPrice: number
  attributes: Record<string, string>
  weightGram: number
  images?: ProductImageRequest[]
}

export interface ProductCreateRequest {
  name: string
  slug: string
  description?: string
  categoryId?: string
  productVariant: ProductVariantRequest[]
}



export interface PresignedUrlResponse {
  url: string
}