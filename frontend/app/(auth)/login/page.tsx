"use client"

import Image from "next/image";
import Logo from "@/public/logo.png"
import {zodResolver} from "@hookform/resolvers/zod"
import {useForm} from "react-hook-form"
import {z} from "zod"
import {Button} from "@/components/ui/button"
import {
    Form,
    FormControl,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from "@/components/ui/form"
import {Input} from "@/components/ui/input"
import axios from "axios";
import {useEffect, useState} from "react";
import {useRouter, useSearchParams} from "next/navigation";
import Link from "next/link";
import {Separator} from "@/components/ui/separator";
import {FcGoogle} from "react-icons/fc";

const formSchemaLogin = z.object({
    email: z.string(),
    password: z.string(),
})

const Login = () => {

    const [error, setError] = useState<{ email?: string; password?: string }>({})
    const router = useRouter()
    const searchParams = useSearchParams();

    const formSearch = useForm<z.infer<typeof formSchemaLogin>>({
        resolver: zodResolver(formSchemaLogin),
        defaultValues: {
            email: "",
            password: "",
        },
    })

    useEffect(() => {
        const errorLoginGoogle = searchParams.get("error");
        if (errorLoginGoogle) {
            setError({
                email: errorLoginGoogle,
                password: "",
            });
        }
    }, [searchParams]);

    async function onSubmit(values: z.infer<typeof formSchemaLogin>) {
        axios.post(`${process.env.NEXT_PUBLIC_API_URL}/api/auth/login`, {
            email: values.email,
            password: values.password
        }, {
            withCredentials: true
        }).then(() => {
            router.push("/")
            setError({})
        }).catch((e) => {
            console.log(e)
            if (e.response.data.status === 409) {
                setError({
                    email: e.response.data.message,
                    password: "",
                });
            } else if (e.response.data.validationErrors?.length === 2) {
                setError({
                    email: e.response.data.validationErrors[0] || "",
                    password: e.response.data.validationErrors[1] || "",
                });
            } else {
                if (e.response.data.validationErrors?.[0].includes("Email")) {
                    setError({
                        email: e.response.data?.validationErrors[0] || "",
                        password: "",
                    });
                } else if (e.response.data.validationErrors?.[0].includes("Password")) {
                    setError({
                        email: "",
                        password: e.response.data.validationErrors[0] || "",
                    });
                } else if (e.response.data.message?.trim() === "Invalid password") {
                    setError({
                        email: "",
                        password: e.response.data.message,
                    });
                } else if (e.response.data.message?.trim() === "User not found") {
                    setError({
                        email: e.response.data.message,
                        password: "",
                    });
                }
            }
        });
    }

    return (
        <div className={"flex items-center justify-center h-screen w-full bg-[#f1f1f1]"}>
            <div className={"w-[400px] h-fit bg-white p-5"}>
                <Image src={Logo} alt={"Logo"} width={100} height={100} className={"m-auto"}/>
                <h1 className={"text-center text-4xl font-bold text-[#0E67F9]"}>Login</h1>
                <div>
                    <Form {...formSearch}>
                        <form onSubmit={formSearch.handleSubmit(onSubmit)} className="space-y-8">
                            <FormField
                                control={formSearch.control}
                                name="email"
                                render={({field}) => (
                                    <FormItem>
                                        <FormLabel>Email</FormLabel>
                                        <FormControl>
                                            <Input placeholder="Email" {...field} />
                                        </FormControl>
                                        <FormMessage>{error.email}</FormMessage>
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={formSearch.control}
                                name="password"
                                render={({field}) => (
                                    <FormItem>
                                        <FormLabel>Password</FormLabel>
                                        <FormControl>
                                            <Input type={"password"} placeholder="Password" {...field} />
                                        </FormControl>
                                        <Link href={"/forget-password"}
                                              className={"block text-blue-700 text-sm text-center"}>
                                            Quên mật khẩu?
                                        </Link>
                                        <FormMessage>{error.password}</FormMessage>
                                    </FormItem>
                                )}
                            />
                            <Button type="submit" className={"bg-[#0E67F9] hover:bg-[#0E67F9] w-full"}>Submit</Button>
                            <div>
                                <h1 className={"text-center"}>
                                    Bạn chưa có tài khoản?
                                    <Link href={"/register"} className={"text-blue-700 ml-2"}>
                                        Đăng ký
                                    </Link>
                                </h1>
                            </div>
                            <div className={"flex items-center"}>
                                <Separator className={"flex-1"}/>
                                <h1 className={"px-10"}>Hoặc</h1>
                                <Separator className={"flex-1"}/>
                            </div>
                            <div className={"flex justify-center"}>
                                <Link href={`${process.env.NEXT_PUBLIC_API_URL}/oauth2/authorization/google`}>
                                    <Button type={"button"} className={"bg-white hover:bg-white text-black"}>
                                        <FcGoogle/>
                                        Google
                                    </Button>
                                </Link>
                            </div>
                        </form>
                    </Form>
                </div>
            </div>
        </div>
    )
}

export default Login