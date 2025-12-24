"use client";

import { useState } from "react";
import axios from "axios";
import { Button } from "@/components/ui/button";
import { Loader2 } from "lucide-react";

export default function VendorRegisterPage() {
  const [formData, setFormData] = useState({
    name: "",
    slug: "",
    logo_url: "",
    description: "",
  });
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<{ type: "success" | "error"; text: string } | null>(null);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.name || !formData.slug) {
      setMessage({ type: "error", text: "Please enter Store Name" });
      return;
    }

    setLoading(true);
    setMessage(null);

    try {
      const res = await axios.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/vendor/vendor-registration/me/register`,
        {
          name: formData.name,
          slug: formData.slug,
          logo_url: formData.logo_url || null,
          description: formData.description || null,
        },
        { withCredentials: true }
      );

      setMessage({ type: "success", text: "Registration successful! You can now access the Dashboard." });
      console.log("Registration successful:", res.data);

      setTimeout(() => {
        window.location.href = "/vendor/dashboard";
      }, 2000);
    } catch (err: any) {
      const errorMsg = err.response?.data?.message || "Registration failed. Please try again!";
      setMessage({ type: "error", text: errorMsg });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 py-6 flex flex-col justify-center sm:py-12">
      <div className="relative py-3 sm:max-w-xl sm:mx-auto">
        {/* Gradient shadow */}
        <div className="absolute inset-0 bg-gradient-to-r from-cyan-400 to-sky-500 shadow-lg transform -skew-y-6 sm:skew-y-0 sm:-rotate-6 sm:rounded-3xl"></div>

        {/* Main card */}
        <div className="relative px-4 py-10 bg-white shadow-lg sm:rounded-3xl sm:p-20">
          <div className="max-w-md mx-auto">
            {/* Title */}
            <div className="text-center mb-8">
              <h1 className="text-3xl font-bold text-gray-900 mb-2">Register Your Store</h1>
              <p className="text-gray-600">It only takes 30 seconds to start selling!</p>
            </div>

            {/* Form */}
            <form onSubmit={handleSubmit} className="space-y-6">
              {/* Store Name */}
              <div className="relative">
                <input
                  type="text"
                  name="name"
                  value={formData.name}
                  onChange={handleChange}
                  className="peer placeholder-transparent h-12 w-full border-b-2 border-gray-300 text-gray-900 focus:outline-none focus:border-cyan-500"
                  placeholder="Store Name"
                  required
                />
                <label className="absolute left-0 -top-3.5 text-gray-600 text-sm peer-placeholder-shown:text-base peer-placeholder-shown:text-gray-440 peer-placeholder-shown:top-3 transition-all peer-focus:-top-3.5 peer-focus:text-cyan-600 peer-focus:text-sm">
                  Store Name *
                </label>
              </div>

              {/* Slug */}
              <div className="relative">
                <input
                  type="text"
                  name="slug"
                  value={formData.slug}
                  onChange={handleChange}
                  className="peer placeholder-transparent h-12 w-full border-b-2 border-gray-300 text-gray-900 focus:outline-none focus:border-cyan-500"
                  placeholder="Slug (URL path)"
                  required
                />
                <label className="absolute left-0 -top-3.5 text-gray-600 text-sm peer-placeholder-shown:text-base peer-placeholder-shown:text-gray-440 peer-placeholder-shown:top-3 transition-all peer-focus:-top-3.5 peer-focus:text-cyan-600 peer-focus:text-sm">
                  Slug(e.g., my-shop)
                </label>
              </div>

              {/* Logo URL */}
              <div className="relative">
                <input
                  type="url"
                  name="logo_url"
                  value={formData.logo_url}
                  onChange={handleChange}
                  className="peer placeholder-transparent h-12 w-full border-b-2 border-gray-300 text-gray-900 focus:outline-none focus:border-cyan-500"
                  placeholder="Logo URL (optional)"
                />
                <label className="absolute left-0 -top-3.5 text-gray-600 text-sm peer-placeholder-shown:text-base peer-placeholder-shown:text-gray-440 peer-placeholder-shown:top-3 transition-all peer-focus:-top-3.5 peer-focus:text-cyan-600 peer-focus:text-sm">
                  Logo URL (optional)
                </label>
              </div>

              {/* Description */}
              <div className="relative">
                <textarea
                  name="description"
                  value={formData.description}
                  onChange={handleChange}
                  rows={3}
                  className="peer placeholder-transparent w-full border-2 border-gray-300 rounded-md px-1 pt-3 text-gray-900 focus:outline-none focus:border-cyan-500 resize-none"
                  placeholder="Store Description"
                />
                <label className="absolute left-2 -top-2.5 bg-white px-1 text-gray-600 text-sm peer-placeholder-shown:text-base peer-placeholder-shown:top-3 peer-focus:-top-2.5 peer-focus:text-cyan-600 peer-focus:text-sm transition-all">
                  Store Description (optional)
                </label>
              </div>

              {/* Submit Button */}
              <Button
                type="submit"
                disabled={loading}
                className="w-full h-12 text-lg bg-cyan-500 hover:bg-cyan-600"
              >
                {loading ? (
                  <>
                    <Loader2 className="mr-2 h-5 w-5 animate-spin" />
                    Registering...
                  </>
                ) : (
                  "Register Store"
                )}
              </Button>
            </form>

            {/* Message */}
            {message && (
              <div
                className={`mt-6 text-center p-4 rounded-lg ${
                  message.type === "success" ? "bg-green-100 text-green-800" : "bg-red-100 text-red-800"
                }`}
              >
                {message.text}
              </div>
            )}

            <p className="text-center text-xs text-gray-600 mt-8 opacity-80">
              After registration, you will be redirected to the Dashboard to complete detailed information.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}