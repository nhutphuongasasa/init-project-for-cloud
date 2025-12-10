"use client"; // cần nếu gọi API từ client component

import React, { useState } from "react";
import { getMyVendor, registerVendor, testLogin } from "../api/vendor/RegistrationApi";
import { Button } from "@/components/ui/button";
import { approveVendor, getAllVendor, getPendingVendors, searchVendor } from "../api/vendor/AdminApi";
import { createCategory, deleteCategory, getAllCategories, getCategoryBySlug,  } from "../api/product/CategoryApi";
import { addProductVariant, getpreurl, removeProductVariant, updateProductVariantBaseInfo } from "../api/product/ProductCommandApi";
import axios from "axios";
import { getAllProductAdmin, getMyActiveProducts, getMyProducts, getproductById, searchAllProductAdmin, searchMyproduct } from "../api/product/ProductQueryApi";
import { get } from "http";
import { addStock, adjstock, createInbound, releaseStock, reserveStock, returnStock, tranfer } from "../api/inventory/InventoryCommandApi";
import { getMovement, getMyWarehouses, getStockDetail, getStockReport } from "../api/inventory/InventoryQueryApi";
import { approveOrder, cancelInbound, cancelOrder, completePacking, completePicking, completeReceiving, confirmInbound, createInboundOrder, createOrder, shipOrder, startPacking, startPicking, startReceiving, updatePickStockquantity } from "../api/order/OrderCommandApi";
import { getAllOrders, getMyOrder, getMyOrderDetail, getOrderDetail } from "../api/order/OrderQueryApi";
import { updateBasicInfo, updateProfile } from "../api/vendor/ProfileApi";

const TestPage = () => {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);

   const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setSelectedFile(e.target.files[0]);
    }
  };

  const handleUpload = async () => {
    if (!selectedFile) {
      console.error("No file selected");
      return;
    }

    try {
      const { url } = await getpreurl(selectedFile.name);

      // await axios.put(url, selectedFile, {
      //   headers: { "Content-Type": selectedFile.type },
      // });

      const imageUrl = url.split("?")[0];
      console.log("Uploaded file URL:", imageUrl);

      const productRes = await axios.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/product/products-command`,
        {
          name: "phuong",
          slug: "phuong",
          description: "phuong",
          categoryId: "1b21b65a-faad-4626-8e8d-aa98907431e6",
          productVariant: [
            {
              price: 100000,
              originalPrice: 120000,
              attributes: {
                color: "red",
                size: "M"
              },
              weightGram: 500,
              images: [
                {
                  url: "https://project-okella.s3.us-east-2.amazonaws.com/0a546e8b-ef65-47ae-9d90-f9998042b56b-ai-generated-9917901_1280.png",
                  isMain: false
                },
                {
                  url: "https://project-okella.s3.us-east-2.amazonaws.com/290761c8-5d8c-430c-8f9b-f3125bca5963-ai-generated-9917901_1280.png",
                  isMain: false
                }
              ],
          },
          {
            price: 100000,
            originalPrice: 120000,
            attributes: {
              color: "blue",
              size: "L"
            },
            weightGram: 500,
            images: [
              {
                url: "https://project-okella.s3.us-east-2.amazonaws.com/d86a1586-1194-4ed8-8309-405ed3db149e-ai-generated-9917901_1280.png",
                isMain: false
              }
            ],
          }
          ],
        },
        { withCredentials: true }
      );

      console.log("Product created:", productRes.data);
    } catch (err) {
      console.error("Upload failed:", err);
    }
  };

  const handleTestLogin = () => {
    testLogin();
  }

  const handleRegister = async () => {
    try {
      const res = await registerVendor();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleGetMyVendor = async () => {
    try {
      const res = await getMyVendor();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleApproveVendor = async () => {
    try {
      const res = await approveVendor();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleGetPendingVendors = async () => {
    try {
      const res = await getPendingVendors();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleSearchVendor = async () => {
    try {
      const res = await searchVendor();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleUpdateProfile = async () => {
    try {
      const res = await updateProfile();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleUpdateBasicInfo = async () => {
    try {
      const res = await updateBasicInfo();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleCreateCategory = async () => {
    try {
      const res = await createCategory();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleGetAllCategories = async () => {
    try {
      const res = await getAllCategories();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleAddProductVariant = async () => {
    try {
      const res = await addProductVariant();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleRemoveProductVariant = async () => {
    try {
      const res = await removeProductVariant();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleUpdateProductVariantBaseInfo = async () => {
    try {
      const res = await updateProductVariantBaseInfo();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleGetMyProducts = async () => {
    try {
      const res = await getMyProducts();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleGetMyActiveProducts = async () => {
    try {
      const res = await getMyActiveProducts();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleGetMyProductById = async () => {
    try {
      const res = await getproductById();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleSearchAllProductAdmin = async () => {
    try {
      const res = await searchAllProductAdmin();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleSearchMyProduct = async () => {
    try {
      const res = await searchMyproduct();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleGetAllProductAdmin = async () => {
    try {
      const res = await getAllProductAdmin();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };


  const handleGetCategoryBySlug = async () => {
    try {
      const res = await getCategoryBySlug();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleDeleteCategory = async () => {
    try {
      const res = await deleteCategory();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleCreateInbound = async () => {
    try {
      const res = await createInbound();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleAdjustStock = async () => {
    try {
      const res = await adjstock();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleAddStock = async () => {
    try {
      const res = await addStock();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleReserveStock = async () => {
    try {
      const res = await reserveStock();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleReleaseStock = async () => {
    try {
      const res = await releaseStock();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };


  const handleReturnStock = async () => {
    try {
      const res = await returnStock();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleTransfer = async () => {
    try {
      const res = await tranfer();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleGetStockDetail = async () => {
    try {
      const res = await getStockDetail();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleGetMovement = async () => {
    try {
      const res = await getMovement();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleGetStockReport = async () => {
    try {
      const res = await getStockReport();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleGetMyWarehouses = async () => {
    try {
      const res = await getMyWarehouses();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleCreateOrder = async () => {
    try {
      const res = await createOrder();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleApproveOrder = async () => {
    try {
      const res = await approveOrder();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleUpdatePickStockquantity = async () => {
    try {
      const res = await updatePickStockquantity();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleShipOrder = async () => {
    try {
      const res = await shipOrder();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleCancelOrder = async () => {
    try {
      const res = await cancelOrder();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleStartPicking = async () => {
    try {
      const res = await startPicking();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleStartPacking = async () => {
    try {
      const res = await startPacking();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleCompletePicking = async () => {
    try {
      const res = await completePicking();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleCompletePacking = async () => {
    try {
      const res = await completePacking();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleCreateInboundOrder = async () => {
    try {
      const res = await createInboundOrder();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleConfirmInboundOrder = async () => {
    try {
      const res = await confirmInbound();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleStartReceivingOrder = async () => {
    try {
      const res = await startReceiving();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleCompleteReceivingOrder = async () => {
    try {
      const res = await completeReceiving();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleCancelInboundOrder = async () => {
    try {
      const res = await cancelInbound();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };



  const handleGetAllMyOrders = async () => {
    try {
      const res = await getMyOrder();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };


  const handleGetAllOrders = async () => {
    try {
      const res = await getAllOrders();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

    const handleGetOrderDetail = async () => {
    try {
      const res = await getOrderDetail();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

      const handleGetMyOrderDetail = async () => {
    try {
      const res = await getMyOrderDetail();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };

  const handleGetAllVendor = async () => {
    try {
      const res = await getAllVendor();
      console.log("Response:", res);
    } catch (err) {
      console.error("Error:", err);
    }
  };


  return (
    <div className="flex flex-col justify-center items-center h-screen gap-4">
      <div>Registration</div>
      <div className="flex gap-2">
        <Button onClick={handleTestLogin}>Test Login</Button>
        <Button onClick={handleRegister}>Register Vendor</Button>
        <Button onClick={handleGetMyVendor}>Get My Vendor</Button>
      </div>

      <div>Admin</div>
      <div className="flex gap-2">
        <Button onClick={handleGetAllVendor}>Get All Vendor</Button>
        <Button onClick={handleApproveVendor}>Approve Vendor</Button>
        <Button onClick={handleGetPendingVendors}>Get Pending Vendors</Button>
        <Button onClick={handleSearchVendor}>Search Vendor</Button>
      </div>

      <div>Profile</div>
      <div className="flex gap-2">
        <Button onClick={handleUpdateBasicInfo}>Update Basic Info</Button>
        <Button onClick={handleUpdateProfile}>Update Profile</Button>
      </div>

      <div>Category</div>
      <div className="flex gap-2">
        <Button onClick={handleCreateCategory}>Create Category</Button>
        <Button onClick={handleGetAllCategories}>Get All Categories</Button>
        <Button onClick={handleGetCategoryBySlug}>Get Category By Slug</Button>
        <Button onClick={handleDeleteCategory}>Delete Category</Button>
      </div>

      <div>Product</div>
      <div className="flex gap-2">
        <input type="file" onChange={handleFileChange} />
        <Button onClick={handleUpload}>Upload & Create Product</Button>
      </div>

      <div>Product variant</div>
      <div className="flex gap-2">
        <Button onClick={handleAddProductVariant}>Add Product Variant</Button>
        <Button onClick={handleRemoveProductVariant}>Remove Product Variant</Button>
        <Button onClick={handleUpdateProductVariantBaseInfo}>Update Product Variant Base Info</Button>
      </div>

      <div>Search Product</div>
      <div className="flex gap-2">
        <Button onClick={handleGetMyProducts}>Get My Products</Button>
        <Button onClick={handleGetMyActiveProducts}>Get My Active Products</Button>
        <Button onClick={handleGetMyProductById}>Get My Product By Id</Button>
        <Button onClick={handleSearchMyProduct}>Search My Product</Button>
        <Button onClick={handleSearchAllProductAdmin}>Search All Product Admin</Button>
        <Button onClick={handleGetAllProductAdmin}>Get All Product Admin</Button>
      </div>

      <div>Inventory</div>
      <div className="flex gap-2">
        <Button onClick={handleCreateInbound}>Create Inbound</Button>
        <Button onClick={handleAdjustStock}>Adjust Stock</Button>
        <Button onClick={handleAddStock}>Add Stock</Button>
        <Button onClick={handleReserveStock}>Reserve Stock</Button>
        <Button onClick={handleReleaseStock}>Release Stock</Button>
        <Button onClick={handleReturnStock}>Return Stock</Button>
        <Button onClick={handleTransfer}>Transfer</Button>
      </div>

      <div className="flex gap-2">
        <Button onClick={handleGetStockDetail}>Get Stock Detail</Button>
        <Button onClick={handleGetMovement}>Get Movement</Button>
        <Button onClick={handleGetStockReport}>Get Stock Report</Button>
        <Button onClick={handleGetMyWarehouses}>Get My Warehouses</Button>
      </div>

      <div>Order</div>
      <div className="flex gap-2">
        <Button onClick={handleCreateOrder}>Create Order</Button>
        <Button onClick={handleApproveOrder}>Approve Order</Button>
        <Button onClick={handleUpdatePickStockquantity}>Update Pick Stock Quantity</Button>
        <Button onClick={handleShipOrder}>Ship Order</Button>
        <Button onClick={handleCancelOrder}>Cancel Order</Button>
        <Button onClick={handleStartPicking}>Start Picking</Button>
        <Button onClick={handleCompletePicking}>Complete Picking</Button>
        <Button onClick={handleStartPacking}>Start Packing</Button>
        <Button onClick={handleCompletePacking}>Complete Packing</Button>
      </div>

      <div className="flex gap-2">
        <Button onClick={handleCreateInboundOrder}>Create Inbound Order</Button>
        <Button onClick={handleConfirmInboundOrder}>Confirm Inbound Order</Button>
        <Button onClick={handleStartReceivingOrder}>Start Receiving Order</Button>
        <Button onClick={handleCompleteReceivingOrder}>Complete Receiving Order</Button>
        <Button onClick={handleCancelInboundOrder}>Cancel Inbound Order</Button>
      </div>

      <div className="flex gap-2">
        <Button onClick={handleGetAllMyOrders}>Get All My Orders</Button>
        <Button onClick={handleGetAllOrders}>Get All Orders</Button>
        <Button onClick={handleGetOrderDetail}>Get Order Detail</Button>
        <Button onClick={handleGetMyOrderDetail}>Get My Order Detail</Button>
      </div>
    </div>
  )
}

export default TestPage
