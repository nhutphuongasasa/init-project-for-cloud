"use client"

import { Header } from '@/components/custom/header';
import StatusDisplay from '@/components/custom/status-display';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Card, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Facebook, Globe, Instagram, Linkedin, Mail, MapPin, Phone, Twitter } from 'lucide-react';
import React, { useEffect, useState } from 'react'
import { Vendor } from '@/interface/vendor';
import { Product, ProductVariant } from '@/interface/product';
import api from '@/lib/axios';
import ProductCard from '@/components/custom/product/product-card-basic';

const VendorDetailPage = ({ params }: { params: Promise<{ id: string }> }) => {
    const param = React.use(params)
    const vendorId = param.id;
    const [products, setProducts] = useState<ProductVariant[]>([])

    const [vendor, setVendor] = useState<Vendor>({
        id: "phuong",
        name: "phuongs",
        slug: "ojad",
        description: "ajsdksajd",
        logoUrl: "https://project-okella.s3.us-east-2.amazonaws.com/061d503b-bb20-4115-8860-0208578c7fe9-ai-generated-9917901_1280.png",
        status: "ACTIVE",
        joinedAt: "bkasdbkasj",
        profile: {
            address: "address",
            phone: "phone",
            email: "email",
            websiteUrl: "webUrl",
            socialLinks: {
                facebook: "facebook",
                twitter: "twitter",
                instagram: "instagram",
                youtube: "youtube"
            }
        }
    })

    //sua api lai
    const fetchProducts = async () => {
        try {
        const res = await api.get("/api/product/products-query")
        const content = res.data?.data?.content || []
        console.log(res.data)
        setProducts(content)
        } catch (err) {
        console.error("Lỗi load sản phẩm:", err)
        console.log(err)
        setProducts([])
        } finally {
        }
    }

    const fetchVendor = async () => {
        try {
            const res = await api.get(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/vendor/admin/${vendorId}`, 
                { withCredentials: true }
            );
            console.log(res.data)
            setVendor(res.data.data);
        } catch (err: any) {
            console.error("Lỗi load vendor:", err)
            alert(err.response?.data?.message || "Lỗi kết nối API");
        }
    }

    useEffect(() => {
        fetchVendor()
        fetchProducts()
    }, [vendorId])
    return (
    <div className="w-full min-h-screen bg-background flex flex-col">
        <Header title="Vendor" subtitle="vendor detail" />

        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-5 p-2">
            <Card className="flex gap-2 items-center px-2 py-2 justify-center">
                <Avatar className="w-12 h-12">
                <AvatarImage src={vendor.logoUrl} />
                <AvatarFallback>JS</AvatarFallback>
                </Avatar>
                <CardHeader>
                <CardTitle>name: {vendor.name}</CardTitle>
                <CardDescription>slug: {vendor.slug}</CardDescription>
                <StatusDisplay status={vendor.status} />
                </CardHeader>
            </Card>

            <div className="p-4 flex flex-col gap-3 bg-card rounded-lg border border-slate-200 shadow-sm">
                <div className="flex items-center gap-2 text-sm">
                    <MapPin className="w-4 h-4 text-primary" />
                    <span className="font-semibold">Address:</span>
                    <p className="text-muted-foreground">{vendor.profile.address}</p>
                </div>
                <div className="flex items-center gap-2 text-sm">
                    <Phone className="w-4 h-4 text-primary" />
                    <span className="font-semibold">Phone:</span>
                    <p className="text-muted-foreground">{vendor.profile.phone}</p>
                </div>
                <div className="flex items-center gap-2 text-sm">
                    <Mail className="w-4 h-4 text-primary" />
                    <span className="font-semibold">Email:</span>
                    <p className="text-muted-foreground">{vendor.profile.email}</p>
                </div>
                <div className="flex items-center gap-2 text-sm">
                    <Globe className="w-4 h-4 text-primary" />
                    <span className="font-semibold">Web Link:</span>
                    <p className="text-muted-foreground">{vendor.profile.websiteUrl}</p>
                </div>
            </div>
            <div className="flex flex-col gap-3 bg-card rounded-lg border border-slate-200 shadow-sm p-4">
                <div className="flex items-center gap-2">
                    <Globe className="w-4 h-4 text-primary" />
                    <span className="font-semibold">Social Link</span>
                </div>
                {Object.keys(vendor.profile.socialLinks).length > 0 &&
                    Object.entries(vendor.profile.socialLinks).map(([key, value]) => {
                    let Icon
                    switch (key.toLowerCase()) {
                        case "facebook":
                        Icon = Facebook
                        break
                        case "twitter":
                        Icon = Twitter
                        break
                        case "instagram":
                        Icon = Instagram
                        break
                        case "linkedin":
                        Icon = Linkedin
                        break
                        default:
                        Icon = Globe
                    }

                    return (
                        <div key={key} className="flex items-center gap-2 text-sm">
                        <Icon className="w-4 h-4 text-primary" />
                        <span className="font-semibold capitalize">{key}:</span>
                        <a
                            href={value}
                            target="_blank"
                            rel="noopener noreferrer"
                            className="text-muted-foreground hover:text-primary transition-colors"
                        >
                            {value}
                        </a>
                        </div>
                    )
                    })}
            </div>
        </div>

        <div className='pt-5 flex-1 bg-gray-50 w-full h-full'>
            <div className='p-4 flex flex-col gap-4 overflow-auto'>
                {products.map((product) => (
                    <ProductCard
                        key={product.id}
                        product={product}
                    />
                ))}
            </div>
        </div>
    </div>
    )
}

export default VendorDetailPage