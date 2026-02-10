import {Button} from "@/app/Input/Button";
import {RequestMethod} from "@/app/resources/data";
import {getBcBlockColor, getBorderColor} from "@/app/resources/utils";

interface RequestItemProps {
    method: RequestMethod;
    url: string;
}

export const RequestItem = ({
    method,
    url
} : RequestItemProps) => {
    return (
        <div className={`flex items-center p-3 border flex-1 rounded-xl justify-between
                ${getBorderColor(method)}
                ${getBcBlockColor(method)}
            `}
        >
            <span>{url}</span>
            <Button method={method}/>
        </div>
    );
}