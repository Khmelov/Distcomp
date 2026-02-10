"use client"
import {RequestItem} from "@/app/Request/RequestItem";
import {IRequestItem} from "@/app/resources/data";
import {RequestHeader} from "@/app/Request/RequestHeader";
import {useMemo, useState} from "react";
import {RequestModal} from "@/app/Request/RequestModal";

interface RequestBlockProps {
    header: string;
    items: IRequestItem[];
    apiVersion: string;
}

const NOT_SELECT : number = -1;

export const RequestBlock = ({
    header,
    items,
    apiVersion
} : RequestBlockProps) => {
    const [selected, setSelected] = useState<number>(NOT_SELECT);
    const isModalOpen = useMemo(() => selected != NOT_SELECT, [selected]);

    const selectedItem = useMemo(() =>
            selected !== NOT_SELECT ? items[selected] : undefined,
        [selected, items]
    );

    const handleClose = () => {
        setSelected(NOT_SELECT);
    };

    return (
        <>
            <div className="flex flex-1 flex-col">
                <RequestHeader>
                    {header}
                </RequestHeader>
                <div className="flex flex-1 flex-col gap-2">
                    {items.map((item: IRequestItem, id: number) => (
                        <RequestItem key={id} method={item.method} url={item.url} onClick={() => setSelected(id)}/>
                    ))}
                </div>
            </div>

            <RequestModal
                isOpen={isModalOpen}
                requestItem={selectedItem}
                onClose={handleClose}
                apiVersion={apiVersion}
            />
        </>
    )
}