import axios from "axios";

export async function createInbound() {
    const res = await axios.post(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/inventory/inventory-command/inbound`, {
        productVariantId: "66666666-6666-6666-6666-666666666666",
        warehouseId: "11111111-1111-1111-1111-111111111111",
        vendorId: "c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b",
        quantityAvailable: 10,
        quantityReserved: 0,
        safetyStock: 10
    }, {
        withCredentials: true
    })

    return res.data;
}
//INBOUND, RETURN
//ADJUSTMENT
//OUTBOUND
//TRANSFER

export async function adjstock() {
    const res = await axios.post(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/inventory/inventory-command/adjust`, {
        productVariantId: "66666666-6666-6666-6666-666666666666",
        warehouseId: "11111111-1111-1111-1111-111111111111",
        vendorId: "c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b",
        quantity: 10,
        notes: "Adjust stock",
        type: "ADJUSTMENT",
        referenceType: "ADJUSTMENT",
        createdBy: "vendor:c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b"
    }, {
        withCredentials: true
    })

    return res.data;
}

export async function addStock() {
    const res = await axios.post(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/inventory/inventory-command/adjust`, {
        productVariantId: "66666666-6666-6666-6666-666666666666",
        warehouseId: "11111111-1111-1111-1111-111111111111",
        vendorId: "c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b",
        quantity: 10,
        notes: "add stock",
        type: "INBOUND",
        referenceType: "INBOUND",
        createdBy: "vendor:c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b"
    }, {
        withCredentials: true
    })

    return res.data;
}

export async function reserveStock() {
    const res = await axios.post(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/inventory/inventory-command/adjust`, {
        productVariantId: "66666666-6666-6666-6666-666666666666",
        warehouseId: "11111111-1111-1111-1111-111111111111",
        vendorId: "c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b",
        quantity: 1,
        notes: "remove stock",
        type: "OUTBOUND",
        referenceType: "ORDER_RESERVE",
        createdBy: "vendor:c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b"
    }, {
        withCredentials: true
    })

    return res.data;
}

export async function releaseStock() {
    const res = await axios.post(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/inventory/inventory-command/adjust`, {
        productVariantId: "66666666-6666-6666-6666-666666666666",
        warehouseId: "11111111-1111-1111-1111-111111111111",
        vendorId: "c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b",
        quantity: 1,
        notes: "remove stock",
        type: "OUTBOUND",
        referenceType: "ORDER_RELEASE",
        createdBy: "vendor:c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b"
    }, {
        withCredentials: true
    })

    return res.data;
}

export async function returnStock() {
    const res = await axios.post(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/inventory/inventory-command/adjust`, {
        productVariantId: "66666666-6666-6666-6666-666666666666",
        warehouseId: "11111111-1111-1111-1111-111111111111",
        vendorId: "c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b",
        quantity: 1,
        notes: "return stock",
        type: "RETURN",
        createdBy: "vendor:c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b"
    }, {
        withCredentials: true
    })

    return res.data;
}



export async function tranfer() {
    const res = await axios.post(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/inventory/inventory-command/transfer`, {
        variantId: "66666666-6666-6666-6666-666666666666",
        fromWarehouseId: "11111111-1111-1111-1111-111111111111",
        toWarehouseId: "22222222-2222-2222-2222-222222222222",
        quantity: 10,
        notes: "Transfer stock",
        // createdBy: "vendor:c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b"
    }, {
        withCredentials: true
    })

    return res.data;
}