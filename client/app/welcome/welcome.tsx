import { Formik } from "formik";
import { Shield, Mail, Lock, EyeOff, Eye, User } from "lucide-react";
import { useState } from "react";
import FormController from "~/utils/FormController";


export function Welcome() {
  const [isLogin, setIsLogin] = useState<boolean>(true);
  const [formData, setFormData] = useState<{
    email: string;
    password: string;
    confirmPassword?: string;
    name?: string;
  }>({
    email: '',
    password: ''
  });
  const [showPassword, setShowPassword] = useState<boolean>(false);

  const handleInputChange = (e: { target: { name: any; value: any; }; }) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value
    }));
  };

  const loginForm = [
    {
      name: 'email',
      labelName: 'Email Address',
      type: 'email',
      required: true,
      fieldGroupClassName: "animate-in slide-in-from-top duration-300",
      labelClassName: "block text-sm font-medium text-gray-700 mb-2",
      fieldIcon: <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />,
      innerGroupClassName: "relative",
      inputClassName: "w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200",
      placeholder: "johndoe@example.com",
      errorClassName: "text-red-500 text-xs mt-1 animate-in slide-in-from-top duration-200",
      value: formData?.email,
      onChange: handleInputChange,
    },
    {
      name: 'password',
      labelName: 'Password',
      type: 'password',
      required: true,
      fieldGroupClassName: "animate-in slide-in-from-top duration-300",
      labelClassName: "block text-sm font-medium text-gray-700 mb-2",
      fieldIcon: <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />,
      innerGroupClassName: "relative",
      inputClassName: "w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200",
      placeholder: "*********",
      errorClassName: "text-red-500 text-xs mt-1 animate-in slide-in-from-top duration-200",
      value: formData?.password,
      onChange: handleInputChange,
      passwordToggleClassName: "absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors duration-200",
      passwordToggleIcon: showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />,
      onClickPasswordToggle: () => setShowPassword(!showPassword)
    },
  ];

  const registrationForm = [
    {
      name: 'name',
      labelName: 'Full Name',
      type: 'text',
      required: true,
      fieldGroupClassName: "animate-in slide-in-from-top duration-300",
      labelClassName: "block text-sm font-medium text-gray-700 mb-2",
      fieldIcon: <User className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />,
      innerGroupClassName: "relative",
      inputClassName: "w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200",
      placeholder: "John Doe",
      errorClassName: "text-red-500 text-xs mt-1 animate-in slide-in-from-top duration-200",
      value: formData?.name,
      onChange: handleInputChange,
    },
    ...loginForm,
    {
      name: 'confirmPassword',
      labelName: 'Confirm Password',
      type: 'password',
      required: true,
      fieldGroupClassName: "animate-in slide-in-from-top duration-300",
      labelClassName: "block text-sm font-medium text-gray-700 mb-2",
      fieldIcon: <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />,
      innerGroupClassName: "relative",
      inputClassName: "w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200",
      placeholder: "*********",
      errorClassName: "text-red-500 text-xs mt-1 animate-in slide-in-from-top duration-200",
      value: formData?.confirmPassword,
      onChange: handleInputChange,
      passwordToggleClassName: "absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors duration-200",
      passwordToggleIcon: showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />,
      onClickPasswordToggle: () => setShowPassword(!showPassword)
    },
  ]



  const toggleMode = () => {
    setIsLogin(!isLogin);
  };

  return (
    <main className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-blue-100 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        {/* Header */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 bg-blue-600 rounded-2xl mb-4 shadow-lg">
            <Shield className="w-8 h-8 text-white" />
          </div>
          <h1 className="text-3xl font-bold text-gray-900 mb-2">SecureBank</h1>
          <p className="text-gray-600">Your trusted microbanking partner</p>
        </div>

        {/* Main Card */}
        <div className="bg-white rounded-3xl shadow-2xl border border-gray-100 overflow-hidden">
          {/* Toggle Tabs */}
          <div className="flex bg-gray-50">
            <button
              onClick={() => !isLogin && toggleMode()}
              className={`flex-1 py-4 px-6 text-sm font-semibold transition-all duration-300 ${isLogin
                ? 'bg-white text-blue-600 shadow-sm border-b-2 border-blue-600'
                : 'text-gray-500 hover:text-gray-700'
                }`}
            >
              Sign In
            </button>
            <button
              onClick={() => isLogin && toggleMode()}
              className={`flex-1 py-4 px-6 text-sm font-semibold transition-all duration-300 ${!isLogin
                ? 'bg-white text-blue-600 shadow-sm border-b-2 border-blue-600'
                : 'text-gray-500 hover:text-gray-700'
                }`}
            >
              Create Account
            </button>
          </div>

          <div className="p-8">
            {isLogin &&
              <Formik
                initialValues={{ email: '', password: '' }}
                validate={(values) => { }}
                onSubmit={(values, setSubmitting) => {

                }}
              >
                <form className="space-y-6" >
                  <FormController
                    formData={[
                      ...loginForm,
                      {
                        type: 'submit',
                        text: 'Sign In',
                        buttonClassName: "w-full bg-gradient-to-r from-blue-600 to-blue-700 text-white font-semibold py-3 px-6 rounded-xl hover:from-blue-700 hover:to-blue-800 focus:ring-4 focus:ring-blue-300 transform hover:scale-[1.02] transition-all duration-200 shadow-lg group",
                        spanClassName: "flex items-center justify-center",
                        iconClassName: "w-4 h-4 ml-2 group-hover:translate-x-1 transition-transform duration-200"
                      }]}
                  />
                </form>
              </Formik>}
            {isLogin && (
              <div className="text-right">
                <button
                  type="button"
                  className="text-sm text-blue-600 hover:text-blue-700 font-medium transition-colors duration-200"
                >
                  Forgot password?
                </button>
              </div>
            )}
            {!isLogin && <Formik
              initialValues={{ email: '', password: '', confirmPassword: '' }}
              validate={(values) => { }}
              onSubmit={(values, setSubmitting) => {

              }}
            >
              <form className="space-y-6" >
                <FormController
                  formData={[...registrationForm, {
                    type: 'submit',
                    text: 'Create Account',
                    buttonClassName: "w-full bg-gradient-to-r from-blue-600 to-blue-700 text-white font-semibold py-3 px-6 rounded-xl hover:from-blue-700 hover:to-blue-800 focus:ring-4 focus:ring-blue-300 transform hover:scale-[1.02] transition-all duration-200 shadow-lg group",
                    spanClassName: "flex items-center justify-center",
                    iconClassName: "w-4 h-4 ml-2 group-hover:translate-x-1 transition-transform duration-200"
                  }]}
                />
              </form>
            </Formik>}
          </div>
        </div>

        {/* Security Notice */}
        <div className="text-center mt-6">
          <p className="text-xs text-gray-500 flex items-center justify-center">
            <Shield className="w-3 h-3 mr-1" />
            Your data is protected with bank-level security
          </p>
        </div>
      </div>

    </main>
  );
}
