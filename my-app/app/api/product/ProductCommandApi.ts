import axios from "axios";

export async function getpreurl(fileName:string) {
    const res = await axios.get(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/product/files/presigned-url?fileName=${fileName}`,
        {
        withCredentials: true,
        }
    );

    console.log("res.data", res.data);

    return res.data;
}
export async function addProductVariant(){
    const productId = "d788587a-43c5-47ba-a89b-8f3b7e2a7330";
    const res = await axios.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/product/products-command/${productId}/variant`,
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
        withCredentials: true,
        }
    );

    console.log("res.data", res.data);

    return res.data;
}

export async function removeProductVariant(){
    const productId = "d788587a-43c5-47ba-a89b-8f3b7e2a7330";
    const variantId = "81884e57-f2be-460b-a1f2-745264e14108";
    const res = await axios.delete(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/product/products-command/${productId}/variant/${variantId}`,
        {
        withCredentials: true,
        }
    );

    console.log("res.data", res.data);

    return res.data;
}

export async function updateProductVariantBaseInfo(){
    const variantId = "31ce4502-e47b-4431-9af9-316f5ae151bd";
    const res = await axios.put(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/product/products-command/${variantId}/variant`,
        {
                price: 1000,
                originalPrice: 1200,
                attributes: {
                    color: "reds",
                    size: "Ms"
                },
                weightGram: 500,
        },
        {
        withCredentials: true,
        }
    );

    console.log("res.data", res.data);

    return res.data;
}


