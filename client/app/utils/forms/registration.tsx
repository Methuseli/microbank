import { Mail, Lock, EyeOff, Eye, User } from "lucide-react";
import type { FormikErrors, FormikTouched } from "formik";

interface RegistrationFormArgs {
  values: { name: string; email: string; password: string; confirmPassword: string };
  errors: FormikErrors<any>;
  touched: FormikTouched<any>;
  handleChange: (e: React.ChangeEvent<any>) => void;
  handleBlur: (e: React.FocusEvent<any>) => void;
  showPassword: boolean;
  setShowPassword: (show: boolean) => void;
}

export function getRegistrationForm({
  values,
  errors,
  touched,
  handleChange,
  handleBlur,
  showPassword,
  setShowPassword,
}: RegistrationFormArgs) {
  return [
    {
      name: "name",
      labelName: "Full Name",
      type: "text",
      required: true,
      fieldGroupClassName: "animate-in slide-in-from-top duration-300",
      labelClassName: "block text-sm font-medium text-gray-700 mb-2",
      fieldIcon: <User className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 w-4 h-4" />,
      innerGroupClassName: "relative",
      inputClassName:
        "w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200",
      placeholder: "John Doe",
      value: values.name,
      onChange: handleChange,
      onBlur: handleBlur,
      errorMessage: touched.name && errors.name ? errors.name : undefined,
      errorClassName: "text-red-500 text-xs mt-1 animate-in slide-in-from-top duration-200",
    },
    {
      name: "email",
      labelName: "Email Address",
      type: "email",
      required: true,
      fieldGroupClassName: "animate-in slide-in-from-top duration-300",
      labelClassName: "block text-sm font-medium text-gray-700 mb-2",
      fieldIcon: <Mail className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 w-4 h-4" />,
      innerGroupClassName: "relative",
      inputClassName:
        "w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200",
      placeholder: "johndoe@example.com",
      value: values.email,
      onChange: handleChange,
      onBlur: handleBlur,
      errorMessage: touched.email && errors.email ? errors.email : undefined,
      errorClassName: "text-red-500 text-xs mt-1 animate-in slide-in-from-top duration-200",
    },
    {
      name: "password",
      labelName: "Password",
      type: showPassword ? "text" : "password",
      required: true,
      fieldGroupClassName: "animate-in slide-in-from-top duration-300",
      labelClassName: "block text-sm font-medium text-gray-700 mb-2",
      fieldIcon: <Lock className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 w-4 h-4" />,
      innerGroupClassName: "relative",
      inputClassName:
        "w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200",
      placeholder: "*********",
      value: values.password,
      onChange: handleChange,
      onBlur: handleBlur,
      errorMessage: touched.password && errors.password ? errors.password : undefined,
      errorClassName: "text-red-500 text-xs mt-1 animate-in slide-in-from-top duration-200",
      passwordToggleClassName:
        "absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors duration-200",
      passwordToggleIcon: showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />,
      onClickPasswordToggle: () => setShowPassword(!showPassword),
    },
    {
      name: "confirmPassword",
      labelName: "Confirm Password",
      type: showPassword ? "text" : "password",
      required: true,
      fieldGroupClassName: "animate-in slide-in-from-top duration-300",
      labelClassName: "block text-sm font-medium text-gray-700 mb-2",
      fieldIcon: <Lock className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 w-4 h-4" />,
      innerGroupClassName: "relative",
      inputClassName:
        "w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200",
      placeholder: "*********",
      value: values.confirmPassword,
      onChange: handleChange,
      onBlur: handleBlur,
      errorMessage: touched.confirmPassword && errors.confirmPassword ? errors.confirmPassword : undefined,
      errorClassName: "text-red-500 text-xs mt-1 animate-in slide-in-from-top duration-200",
      passwordToggleClassName:
        "absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors duration-200",
      passwordToggleIcon: showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />,
      onClickPasswordToggle: () => setShowPassword(!showPassword),
    },
  ];
}
