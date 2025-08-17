import axios from "axios";
import { Form, Formik } from "formik";
import { Shield } from "lucide-react";
import { useState } from "react";
import { baseUrl } from "~/config";
import { registrationSchema } from "~/schema/registration";
import { loginSchema } from "~/schema/login";
import FormController from "~/utils/FormController";
import { getRegistrationForm } from "~/utils/forms/registration";
import { Bounce, ToastContainer, toast } from 'react-toastify';
import { getLoginForm } from "~/utils/forms/login";
import { useAuth } from "~/context/AuthContext";


export function Welcome() {
  const [isLogin, setIsLogin] = useState<boolean>(true);
  const [showPassword, setShowPassword] = useState<boolean>(false);
  const { login } = useAuth();

  const toggleMode = () => {
    setIsLogin(!isLogin);
  };

  const handleRegistration = (values: { name: string; email: string; password: string; confirmPassword: string }, {
    setSubmitting,
    setFieldError,
    resetForm,
  }: {
    setSubmitting: (s: boolean) => void;
    setFieldError: (field: string, message: string | undefined) => void;
    resetForm: () => void;
  }) => {
    setSubmitting(true);
    axios.post(`${baseUrl}auth/register`, values)
      .then((response) => {
        // Handle successful registration
          toast.success("Registration successful! You can now log in.", {
            position: "top-right",
          });
        resetForm();
      })
      .catch((error) => {
        // Handle registration error
        toast.error("Registration failed. Please check your details and try again.", {
          position: "top-right",
        });
        if (error.response) {
          const { field, message } = error.response.data;
          setFieldError(field, message);
        }
      })
      .finally(() => {
        setSubmitting(false);
      });
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
                validationSchema={loginSchema}
                onSubmit={login}
              >
                {({ values, errors, touched, handleChange, handleBlur, isSubmitting, isValid }) => {
                  const loginForm = getLoginForm({
                    values,
                    errors,
                    touched,
                    handleChange,
                    handleBlur,
                    showPassword,
                    setShowPassword,
                  });

                  return (
                    <Form className="space-y-6">
                      <FormController
                        formData={[
                          ...loginForm,
                          {
                            type: "submit",
                            text: isSubmitting ? "Signing In..." : "Sign In",
                            buttonClassName:
                              "w-full bg-gradient-to-r from-blue-600 to-blue-700 text-white font-semibold py-3 px-6 rounded-xl hover:from-blue-700 hover:to-blue-800 focus:ring-4 focus:ring-blue-300 transform hover:scale-[1.02] transition-all duration-200 shadow-lg group disabled:opacity-60",
                            spanClassName: "flex items-center justify-center",
                            iconClassName: "w-4 h-4 ml-2 group-hover:translate-x-1 transition-transform duration-200",
                            disabled: isSubmitting || !isValid,
                            icon: true,
                          },
                        ]}
                      />
                    </Form>
                  );
                }}
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
              initialValues={{ name: '', email: '', password: '', confirmPassword: '' }}
              validationSchema={registrationSchema}
              onSubmit={handleRegistration}
            >

              {({ values, errors, touched, handleChange, handleBlur, isSubmitting, isValid }) => {
                const registrationForm = getRegistrationForm({
                  values,
                  errors,
                  touched,
                  handleChange,
                  handleBlur,
                  showPassword,
                  setShowPassword,
                });

                return (
                  <Form className="space-y-6">
                    <FormController
                      formData={[
                        ...registrationForm,
                        {
                          type: "submit",
                          text: isSubmitting ? "Creating..." : "Create Account",
                          buttonClassName:
                            "w-full bg-gradient-to-r from-blue-600 to-blue-700 text-white font-semibold py-3 px-6 rounded-xl hover:from-blue-700 hover:to-blue-800 focus:ring-4 focus:ring-blue-300 transform hover:scale-[1.02] transition-all duration-200 shadow-lg group disabled:opacity-60",
                          spanClassName: "flex items-center justify-center",
                          iconClassName: "w-4 h-4 ml-2 group-hover:translate-x-1 transition-transform duration-200",
                          disabled: isSubmitting || !isValid,
                          icon: true
                        },
                      ]}
                    />
                  </Form>
                );
              }}
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
      <ToastContainer
        position="top-right"
        autoClose={5000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick={false}
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="light"
        transition={Bounce}
      />
    </main>
  );
}
