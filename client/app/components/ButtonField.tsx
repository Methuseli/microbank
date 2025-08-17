import { ChevronRight } from "lucide-react";

interface ButtonFieldProps {
  type: 'submit' | 'button';
  text: string;
  buttonClassName?: string;
  spanClassName?: string;
  iconClassName?: string;
  disabled?: boolean;
  icon: boolean;
}

function ButtonField(props: Readonly<ButtonFieldProps>) {
  const { type, text, buttonClassName, spanClassName, iconClassName, disabled, icon } = props;
  return (
    <button
      type={type}
      className={buttonClassName}
      disabled={disabled}
    >
      <span className={spanClassName}>
        {text}
        {icon && <ChevronRight className={iconClassName} />}
      </span>
    </button>
  );
}

export default ButtonField;
