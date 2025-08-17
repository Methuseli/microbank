import { DollarSign } from "lucide-react";
import type { FormikErrors, FormikTouched } from "formik";

interface TransactionFormArgs {
  values: { amount: number; description: string; };
  errors: FormikErrors<any>;
  touched: FormikTouched<any>;
  handleChange: (e: React.ChangeEvent<any>) => void;
  handleBlur: (e: React.FocusEvent<any>) => void;
}

export function getTransactionForm({
  values,
  errors,
  touched,
  handleChange,
  handleBlur,
}: TransactionFormArgs) {
  return [
    {
      name: "amount",
      labelName: "Amount",
      type: "number",
      required: true,
      fieldGroupClassName: "",
      labelClassName: "block text-sm font-medium text-gray-700 mb-2",
      fieldIcon: <DollarSign className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />,
      innerGroupClassName: "relative",
      inputClassName:
        "w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200",
      placeholder: "0.00",
      value: values.amount,
      onChange: handleChange,
      onBlur: handleBlur,
      errorMessage: touched.amount && errors.amount ? errors.amount : undefined,
      errorClassName: "text-red-500 text-xs mt-1 animate-in slide-in-from-top duration-200",
    },
    {
      name: "description",
      labelName: "Description",
      type: "text",
      required: true,
      fieldGroupClassName: "",
      labelClassName: "block text-sm font-medium text-gray-700 mb-2",
      innerGroupClassName: "relative",
      inputClassName:
        "w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200",
      placeholder: "Transaction Description",
      value: values.description,
      onChange: handleChange,
      onBlur: handleBlur,
      errorMessage: touched.description && errors.description ? errors.description : undefined,
      errorClassName: "text-red-500 text-xs mt-1 animate-in slide-in-from-top duration-200",
    },
  ];
}


