import {RequestItem} from "@/app/Request/RequestItem";
import {IRequestItem} from "@/app/resources/data";
import {RequestHeader} from "@/app/Request/RequestHeader";

interface RequestBlockProps {
    header: string;
    items: IRequestItem[];
}

export const RequestBlock = ({
    header,
    items,
} : RequestBlockProps) => {
    return (
        <div className="flex flex-1 flex-col">
            <RequestHeader>
                {header}
            </RequestHeader>
            <div className="flex flex-1 flex-col gap-2">
                {items.map((item: IRequestItem, id: number) => (
                    <RequestItem key={id} method={item.method} url={item.url} />
                ))}
            </div>
        </div>
    )
}