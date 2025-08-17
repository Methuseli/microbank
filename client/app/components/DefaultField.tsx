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
  onBlur?: any; // add so Formik can pass
  passwordToggleClassName?: string;
  passwordToggleIcon?: React.ReactNode;
  onClickPasswordToggle?: () => void;
  errorMessage?: string; // <-- new
  step?: string;
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
    onBlur,
    value,
    passwordToggleClassName,
    passwordToggleIcon,
    onClickPasswordToggle,
    errorMessage,
    step
  } = props;


  console.log("Error message: ", name, " ", errorMessage);
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
            className={`${inputClassName}${
                          errorMessage ? 'border-red-300 bg-red-50' : 'border-gray-200 hover:border-gray-300'
                        }`}
            placeholder={placeholder}
            onBlur={onBlur}
            value={value}
            step={type === "number" ? step : undefined}
          />
          {(name === "password" || name === "confirmPassword") && <button
            type="button"
            onClick={onClickPasswordToggle}
            className={passwordToggleClassName}
          >
            {passwordToggleIcon}
          </button>}
        </div>
        {errorMessage && <p className={errorClassName}>{errorMessage}</p>}
      </div>
    </div>
  );
}

export default DefaultField;