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
import {useState} from "react";
import {useRouter} from "next/navigation";
import Link from "next/link";
import {Separator} from "@/components/ui/separator";
import {FcGoogle} from "react-icons/fc";
import {ENV} from "@/config/config";

const formSchemaLogin = z.object({
    username: z.string(),
    email: z.string(),
    password: z.string(),
})


const Register = () => {
    const [error, setError] = useState<{ username?: string, email?: string; password?: string }>({
        email: "",
        password: "",
        username: "",
    })
    const router = useRouter()

    const formSearch = useForm<z.infer<typeof formSchemaLogin>>({
        resolver: zodResolver(formSchemaLogin),
        defaultValues: {
            username: "",
            email: "",
            password: "",
        },
    })

    const updatedError:Partial<Record<string, string | undefined>> = {...error};

    async function onSubmit(values: z.infer<typeof formSchemaLogin>) {
        axios.post(`${ENV.CLIENT_API_URL}/api/auth/register`, {
            username: values.username,
            email: values.email,
            password: values.password
        }, {
            withCredentials: true
        }).then(() => {
            router.push(`/active-account?email=${values.email}`)
            setError({})
        }).catch((e) => {
            updatedError.username = ""
            updatedError.email = ""
            updatedError.password = ""
            if (e.response.data.status === 400) {
                e.response.data.validationErrors.map((item: string) => {
                    updatedError[item.split(" ")[0].toLowerCase()] = item;
                })
            } else if (e.response.data.status === 409) {
                updatedError.email = e.response.data.message
            }
            setError(updatedError);
        });
    }

    return (
        <div className={"flex items-center justify-center h-screen w-full bg-[#f1f1f1]"}>
            <div className={"w-[400px] h-fit bg-white p-5"}>
                <Image src={Logo} alt={"Logo"} width={100} height={100} className={"m-auto"}/>
                <h1 className={"text-center text-4xl font-bold text-[#0E67F9]"}>Sign Up</h1>
                <div>
                    <Form {...formSearch}>
                        <form onSubmit={formSearch.handleSubmit(onSubmit)} className="space-y-8">
                            <FormField
                                control={formSearch.control}
                                name="username"
                                render={({field}) => (
                                    <FormItem>
                                        <FormLabel>Username</FormLabel>
                                        <FormControl>
                                            <Input placeholder="Username" {...field} />
                                        </FormControl>
                                        <FormMessage>{error.username}</FormMessage>
                                    </FormItem>
                                )}
                            />
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
                                        <FormMessage>{error.password}</FormMessage>
                                    </FormItem>
                                )}
                            />
                            <Button type="submit" className={"bg-[#0E67F9] hover:bg-[#0E67F9] w-full"}>Submit</Button>
                            <div>
                                <h1 className={"text-center"}>
                                    Bạn đã có tài khoản?
                                    <Link href={"/login"} className={"text-blue-700 ml-2"}>
                                        Đăng nhập
                                    </Link>
                                </h1>
                            </div>
                            <div className={"flex items-center"}>
                                <Separator className={"flex-1"}/>
                                <h1 className={"px-10"}>Hoặc</h1>
                                <Separator className={"flex-1"}/>
                            </div>
                            <div className={"flex justify-center"}>
                                <Link href={`${ENV.CLIENT_API_URL}/oauth2/authorization/google`}>
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

export default Register