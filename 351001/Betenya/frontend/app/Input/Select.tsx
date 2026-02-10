interface SelectProps {
    items: string[];
}

export const Select = ({
    items
} : SelectProps) => {
    return (
        <select>
            {items.map((item, index) => (
                <option key={index}>{item}</option>
            ))}
        </select>
    )
}