import axios from "axios"

export async function  createOrder() {
    const res = await axios.post(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command`, {
        customerName: "phuong",
        customerPhone: "02820323",
        shippingAddress: "123 Main St",
        externalRef: "asdsad",
        source: "shopee",
        warehouseId: "11111111-1111-1111-1111-111111111111",
        items: [
            {
                productVariantId: "66666666-6666-6666-6666-666666666666",
                quantity: 3,
                unitPrice: 100000,
                notes: "",
                productName: "product name"
            }
        ]
    }, {
        withCredentials: true
    })

    return res.data;
}

export async function approveOrder(){
    const orderId = "0ae4171a-ca56-4cda-b9fe-e4aecd3914e6"
    const vendorId = "c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b"

    const res = await axios.post(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/approve`,
        {
            orderId:  "37bdfca0-5bd5-48eb-97b4-78b409607aa0",
            vendorId: "c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b"
        },{
            withCredentials: true
        }
    );
    return res.data;
}

export async function updatePickStockquantity(){
    const orderId = ""
    const res = await axios.put(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/update-pick-quantity`, 
    {
        quantityPick : 3
    },
    {
        withCredentials: true
    });
    return res.data;
}


export async function shipOrder(){

    const res = await axios.post(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/ship`,
        {
            orderId:  "37bdfca0-5bd5-48eb-97b4-78b409607aa0",
            vendorId: "c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b"
        },{
            withCredentials: true
        }
    );
    return res.data;
}

export async function cancelOrder() {
    const res = await axios.post(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/cancel`,
        {
            resean: "okela",
            orderId:  "a96585bf-95a4-4c57-bb07-93bc1f923749",
            vendorId: "c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b"
        },{
            withCredentials: true
        }
    );
    return res.data;
}


export async function startPicking() {
    const orderId = "37bdfca0-5bd5-48eb-97b4-78b409607aa0"
    const res = await axios.post(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/${orderId}/start-picking`,
        {
        },{
            withCredentials: true
        }
    );
    return res.data;
}

export async function completePicking() {
    const orderId = "37bdfca0-5bd5-48eb-97b4-78b409607aa0"
    const res = await axios.post(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/${orderId}/complete-picking`,
        {
            items:[
                {
                    detailId: 1,
                    quantityPicked: 1,
                    notes: "okela"
                }
            ]
        },{
            withCredentials: true
        }
    );
    return res.data;
}

export async function startPacking() {
    const orderId = "37bdfca0-5bd5-48eb-97b4-78b409607aa0"
    const res = await axios.post(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/${orderId}/start-packing`,
        {
        },{
            withCredentials: true
        }
    );
    return res.data;
}

export async function completePacking() {
    const orderId = "37bdfca0-5bd5-48eb-97b4-78b409607aa0"
    const res = await axios.post(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/${orderId}/complete-packing`,
        {
        },{
            withCredentials: true
        }
    );
    return res.data;
}


export async function createInboundOrder() {
    const res = await axios.post(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/inbound`,
        {
            warehouseId: "11111111-1111-1111-1111-111111111111  ",
            externalRef: "phuong",
            supplierName: "phuong",
            expectedAt: "2025-12-10",
            items: [
                {
                    productVariantId: "66666666-6666-6666-6666-666666666666",
                    productName: "product name",
                    quantityExpected: 1,
                    unitPrice: 1,
                    notes: "okela"
                }
            ]
        },{
            withCredentials: true
        }
    );
    return res.data;
}

export async function confirmInbound() {
    const inboundId = "90068bce-cbc0-4837-8b61-a90388f89280"
    const res = await axios.post(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/${inboundId}/confirm-inbound`,
        {
        },{
            withCredentials: true
        }
    );
    return res.data;
}

export async function startReceiving() {
    const inboundId = "90068bce-cbc0-4837-8b61-a90388f89280"
    const res = await axios.post(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/${inboundId}/start-receiving`,
        {
        },{
            withCredentials: true
        }
    );
    return res.data;
}

export async function completeReceiving() {
    const inboundId = "90068bce-cbc0-4837-8b61-a90388f89280";
    const res = await axios.post(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/${inboundId}/complete-receiving`,
        {
          
            items: [
                {
                    detailId: 1,
                    quantityReceived: 1,
                    notes: "okela"
                }
            ]
        },{
            withCredentials: true
        }
    );
    return res.data;
}

export async function cancelInbound() {
    const inboundId = "b4f6936e-ec94-45de-a974-4ce2cca2b1bd"
    const res = await axios.post(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-command/${inboundId}/cancel-inbound`,
        {
        },{
            withCredentials: true
        }
    );
    return res.data;
}

