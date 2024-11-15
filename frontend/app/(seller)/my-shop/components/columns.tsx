import {ColumnDef} from "@tanstack/react-table"
import {IProduct} from "@/app/types/types";
import Image from "next/image";
import {Button} from "@/components/ui/button";
import {ArrowUpDown} from "lucide-react";
import React from "react";
import DataTableRowActions from "@/app/(seller)/my-shop/components/row-action";

interface ColumnsProps {
    onUpdate: (product: IProduct) => void,
    onDelete: (product: IProduct) => void,
}

export const getColumns = ({onUpdate, onDelete}: ColumnsProps): ColumnDef<IProduct>[] => [
    {
        accessorKey: "id",
        header: "Id",
        cell: ({row},) => (
            <div className="capitalize">{row.getValue("id")}</div>
        ),
    },
    {
        accessorKey: "name",
        header: "Tên",
        cell: ({row}) => (
            <div className="capitalize">{row.getValue("name")}</div>
        ),
    },
    {
        accessorKey: "image",
        header: "Hình ảnh",
        cell: ({row}) => (
            <div className="capitalize">
                <Image src={row.getValue("image")} alt={"Hình ảnh"} width={100} height={100}/>
            </div>
        ),
    },
    {
        accessorKey: "description",
        header: "Mô tả",
        cell: ({row}) => (
            <div className="capitalize">
                <div className="capitalize">{row.getValue("description")}</div>
            </div>
        ),
    },
    {
        accessorKey: "price",
        header: ({column}) => {
            return (
                <Button
                    variant="ghost"
                    onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
                >
                    Giá
                    <ArrowUpDown/>
                </Button>
            )
        },
        cell: ({row}) => {
            const price = parseFloat(row.getValue("price"))
            const formatted = new Intl.NumberFormat("vi-VN", {
                style: "currency",
                currency: "VND",
            }).format(price)
            return <div className="text-right font-medium">{formatted}</div>
        },
    },
    {
        accessorKey: "quantity",
        header: ({column}) => {
            return (
                <Button
                    variant="ghost"
                    onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
                >
                    Số lượng đã bán
                    <ArrowUpDown/>
                </Button>
            )
        },
        cell: ({row}) => {
            return <div className="text-right font-medium">{row.getValue("quantity")}</div>
        },
    },
    {
        accessorKey: "inventory",
        header: "Số lượng tồn kho",
        cell: ({row}) => (
            <div className="capitalize">
                {row.getValue("inventory")}
            </div>
        ),
    },
    {
        id: "actions",
        enableHiding: false,
        cell: ({row}) =>
            <DataTableRowActions row={row}
                                 onDelete={onDelete}
                                 onUpdate={onUpdate}/>,
    },
]
