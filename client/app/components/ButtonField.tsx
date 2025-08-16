import { ChevronRight } from "lucide-react";

interface ButtonFieldProps {
  type: 'submit' | 'button';
  text: string;
  buttonClassName?: string;
  spanClassName?: string;
  iconClassName?: string;
}

function ButtonField(props: Readonly<ButtonFieldProps>) {
  const { type, text, buttonClassName, spanClassName, iconClassName } = props;
  return (
    <button
      type={type}
      className={buttonClassName}
    >
      <span className={spanClassName}>
        {text}
        <ChevronRight className={iconClassName} />
      </span>
    </button>
  );
}

export default ButtonField;
