import axios from "axios";

export async function registerVendor() {
    const res = await axios.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/vendor/vendor-registration/me/register`,
        {
        name: "testasd",
        slug: "testasd",
        logo_url: "tesasdt",
        description: "tesasft",
        },
        {
        withCredentials: true,
        }
    );

    console.log("res.data", res.data);

    return res.data;
}

export async function testLogin() {
    window.location.href = `${process.env.NEXT_PUBLIC_API_BASE_URL}/okela`;
}

export async function getMyVendor() {
    const res = await axios.get(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/vendor/vendor-registration/me`,
        {
        withCredentials: true,
        }
    );

    console.log("res.data", res.data);

    return res.data;
}