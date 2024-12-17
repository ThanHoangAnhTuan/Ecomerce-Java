import {ColumnDef} from "@tanstack/react-table"
import {IOrderResponse} from "@/app/types/types";
import Image from "next/image";
import React from "react";
import DataTableRowActions from "@/app/(buyer)/my-buyer-order/components/row-action";

interface ColumnsProps {
    onUpdate: (order: IOrderResponse) => void,
}

export const getColumns = ({onUpdate}: ColumnsProps): ColumnDef<IOrderResponse>[] => [
    {
        accessorKey: "id",
        header: "Id",
        cell: ({row},) => (
            <div className="capitalize">{row.getValue("id")}</div>
        ),
    },
    {
        accessorKey: "seller",
        header: "Shop",
        cell: ({row}) => {
            return row.original.orderItemList.map((item) => {
                return <div key={item.id} className="capitalize">{item.product.user.name}</div>
            })
        }
    },
    {
        accessorKey: "orderItemList.product.name",
        header: "Product",
        cell: ({row}) => (
            <div className="capitalize">
                {
                    row.original.orderItemList.map(item => (
                        <div key={item.id} className="capitalize">{item.product.name}</div>
                    ))
                }
            </div>
        ),
    },
    {
        accessorKey: "orderItemList.product.image",
        header: "Image",
        cell: ({row}) => (
            <div className="capitalize">
                {
                    row.original.orderItemList.map(item => (
                        <Image key={item.id} src={item.product.image || ""} alt={item.product.name} width={100}
                               height={100}/>
                    ))
                }
            </div>
        ),
    },
    {
        accessorKey: "orderItemList.product.description",
        header: "Description",
        cell: ({row}) => (
            <div className="capitalize">
                {
                    row.original.orderItemList.map(item => (
                        <div key={item.id} className="capitalize">{item.product.description}</div>
                    ))
                }
            </div>
        ),
    },
    {
        accessorKey: "orderItemList.product.price",
        header: "Price",
        cell: ({row}) => {
            return row.original.orderItemList.map(item => {
                const price = item.product.price
                const formatted = new Intl.NumberFormat("vi-VN", {
                    style: "currency",
                    currency: "VND",
                }).format(price)
                return <div key={item.id} className="text-right font-medium">{formatted}</div>
            })
        },
    },
    {
        accessorKey: "orderItemList.quantity",
        header: "Quantity",
        cell: ({row}) => {
            return <div className="text-right font-medium">
                {
                    row.original.orderItemList.map(item => (
                        <div key={item.id}>{item.quantity}</div>
                    ))
                }
            </div>
        },
    },
    {
        accessorKey: "orderItemList.price",
        header: "Total price",
        cell: ({row}) => {

            const price = row.original.total
            const formatted = new Intl.NumberFormat("vi-VN", {
                style: "currency",
                currency: "VND",
            }).format(price)
            return <div className="text-right font-medium">{formatted}</div>
        },
    },
    {
        accessorKey: "status",
        header: "Status",
        cell: ({row}) => (
            <div className="capitalize">
                <div className="capitalize">{row.getValue("status")}</div>
            </div>
        ),
    },
    {
        id: "actions",
        enableHiding: false,
        cell: ({row}) => {
            const status: string = row.getValue("status")
            if (status === "PENDING") {
                return <DataTableRowActions row={row}
                                            onUpdate={onUpdate}/>
            }
        }
    },
]
