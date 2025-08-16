interface DefaultFieldProps {
  name: string;
  labelName: string;
  type: string;
  required: boolean;
  fieldGroupClassName: string;
  labelClassName: string;
  fieldIcon?: React.ReactNode;
  innerGroupClassName: string;
  inputClassName: string;
  placeholder: string;
  errorClassName: string;
  onChange?: any;
  value?: any;
  passwordToggleClassName?: string;
  passwordToggleIcon?: React.ReactNode;
  onClickPasswordToggle?: () => void;

}

const DefaultField = (props: Readonly<DefaultFieldProps>) => {
  const {
    name,
    labelName,
    type,
    required,
    fieldGroupClassName,
    labelClassName,
    fieldIcon,
    innerGroupClassName,
    inputClassName,
    placeholder,
    errorClassName,
    onChange,
    value,
    passwordToggleClassName,
    passwordToggleIcon,
    onClickPasswordToggle
  } = props;

  return (
    <div
      className={fieldGroupClassName}
    >
      <div>
        <label className={labelClassName}>{labelName}</label>
        <div className={innerGroupClassName}>
          {fieldIcon}
          <input
            type={type}
            name={name}
            required={required}
            onChange={onChange}
            className={inputClassName}
            placeholder={placeholder}
            value={value}
          />
          {type === "password" && <button
            type="button"
            onClick={onClickPasswordToggle}
            className={passwordToggleClassName}
          >
            {passwordToggleIcon}
          </button>}
        </div>
        {
          <p className={errorClassName}> </p>
        }
      </div>
    </div>
  );
}

export default DefaultField;