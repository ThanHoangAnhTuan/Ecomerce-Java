"use client"

import {useRouter, useSearchParams} from "next/navigation";
import Image from "next/image";
import Logo from "@/public/logo.png";
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { z } from "zod"

import { Button } from "@/components/ui/button"
import {
    Form,
    FormControl,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from "@/components/ui/form"
import {
    InputOTP,
    InputOTPGroup,
    InputOTPSlot,
} from "@/components/ui/input-otp"
import axios from "axios";
import {ENV} from "@/config/config";
import {useState} from "react";

const FormSchema = z.object({
    pin: z.string().min(6, {
        message: "Your one-time password must be 6 characters.",
    }),
})

const ActiveAccountClient = () => {
    const router = useRouter()
    const searchParams = useSearchParams();
    const [error, setError] = useState<string>("")

    const form = useForm<z.infer<typeof FormSchema>>({
        resolver: zodResolver(FormSchema),
        defaultValues: {
            pin: "",
        },
    })

    async function onSubmit(values: z.infer<typeof FormSchema>) {
        const email = searchParams.get("email");
        await axios.get(`${ENV.CLIENT_API_URL}/api/auth/activate-account?email=${email}&token=${values.pin}`).then(() => {
            setError("")
            router.push(`/`)
        }).catch((e) => {
            setError(e.response.data.message)
        })
    }

    return (
        <div className={"flex items-center justify-center h-screen w-full bg-[#f1f1f1]"}>
            <div className={"w-[400px] h-fit bg-white p-5"}>
                <Image src={Logo} alt={"Logo"} width={100} height={100} className={"m-auto"}/>
                <h1 className={"text-center text-4xl font-bold text-[#0E67F9]"}>Active account</h1>
                <div>
                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="w-2/3 space-y-6 w-full">
                            <FormField
                                control={form.control}
                                name="pin"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>OTP active account</FormLabel>
                                        <FormControl>
                                            <InputOTP maxLength={6} {...field}>
                                                <InputOTPGroup>
                                                    <InputOTPSlot index={0} />
                                                    <InputOTPSlot index={1} />
                                                    <InputOTPSlot index={2} />
                                                    <InputOTPSlot index={3} />
                                                    <InputOTPSlot index={4} />
                                                    <InputOTPSlot index={5} />
                                                </InputOTPGroup>
                                            </InputOTP>
                                        </FormControl>
                                        <FormMessage>{error}</FormMessage>
                                    </FormItem>
                                )}
                            />

                            <Button type="submit" className={"bg-blue-500 hover:bg-blue-400 w-full"}>Submit</Button>
                        </form>
                    </Form>
                </div>
            </div>
        </div>
    )
}

export default ActiveAccountClient