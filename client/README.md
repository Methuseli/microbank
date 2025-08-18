# SecureBank Frontend - React Router Application

A modern, responsive frontend application for the SecureBank microservice platform. Built with React Router 7, TypeScript, and Tailwind CSS, providing a seamless banking experience with real-time updates and intuitive user interface.

## 🏗️ Frontend Architecture

The application follows a component-based architecture with clear separation of concerns:

```
client/
├── app/
│   ├── components/          # Reusable UI components
│   ├── pages/              # Page components
│   │   ├── welcome/        # Authentication pages
│   │   ├── dashboard/      # User dashboard
│   │   └── admin/          # Admin panel
│   ├── context/            # React Context providers
│   ├── utils/              # Utility functions and forms
│   ├── schema/             # Validation schemas
│   ├── types/              # TypeScript type definitions
│   └── routes/             # Route definitions
├── public/                 # Static assets
└── build/                  # Production build output
```

## 🚀 Why React Router 7?

### 1. **Modern React Framework**
- Built-in TypeScript support
- Server-side rendering capabilities (disabled for SPA mode)
- File-based routing system
- Optimized bundle splitting

### 2. **Performance Benefits**
- Automatic code splitting
- Optimized asset loading
- Built-in caching strategies
- Fast refresh during development

### 3. **Developer Experience**
- Type-safe routing
- Built-in error boundaries
- Hot module replacement
- Excellent debugging tools

### 4. **Production Ready**
- Optimized production builds
- Built-in security features
- SEO optimization capabilities
- Progressive Web App support

## 🛠️ Technology Stack

### Core Framework
- **React Router 7** (Modern React Framework)
- **TypeScript** (Type Safety)
- **Tailwind CSS 4** (Utility-First Styling)

### Form Management
- **Formik** (Form State Management)
- **Yup** (Schema Validation)

### HTTP & State Management
- **Axios** (HTTP Client)
- **React Context** (Global State)

### UI Components
- **Lucide React** (Icon Library)
- **Ant Design** (Select Components)
- **React Toastify** (Notifications)

### Development Tools
- **Vite** (Build Tool)
- **ESLint** (Code Linting)
- **TypeScript Compiler** (Type Checking)

## 📊 Core Features

### Authentication System
- User registration with validation
- Secure login with JWT tokens (Here we store the JWT in local storage, however, this is not secure, with a domain setup we would use HTTP Only cookie instead. This will reduce vulnerabilities like XSS and CSRF)
- Automatic token refresh
- Protected route handling

### User Dashboard
- Real-time account balance display
- Transaction history with pagination
- Deposit and withdrawal operations
- Balance visibility toggle

### Admin Panel
- User management dashboard
- Account blocking/unblocking
- System-wide statistics
- User search and filtering

### Responsive Design
- Mobile-first approach
- Tablet and desktop optimization
- Touch-friendly interactions
- Accessible UI components

## 🏃‍♂️ Running the Application

### Prerequisites
- Node.js 20+
- npm or yarn

### Development Mode
```bash
# Install dependencies
npm install

# Start development server
npm run dev

# Application will be available at http://localhost:5173
```

### Production Build
```bash
# Build for production
npm run build

# Preview production build
npm run preview

# Start production server
npm run start
```

### Docker Development
```bash
# Build Docker image
docker build -t microbank/frontend .

# Run container
docker run -p 3000:3000 microbank/frontend
```

## 🧪 Testing

### Running Tests
```bash
# Run all tests
npm test

# Run tests in watch mode
npm run test:watch

# Run tests with coverage
npm run test:coverage
```

### Test Structure
```
src/
├── __tests__/              # Test files
├── components/__tests__/   # Component tests
└── utils/__tests__/        # Utility tests
```

## ⚙️ Configuration

### Environment Variables
Create a `.env` file in the client directory:

```env
# API Configuration
VITE_API_BASE_URL=http://localhost:8081/api/v1
VITE_BANKING_API_URL=http://localhost:8080/api/v1

# Application Configuration
VITE_APP_NAME=SecureBank
VITE_APP_VERSION=1.0.0
```

### API Configuration
```typescript
// app/config.ts
export const baseUrl = "http://localhost:8081/api/v1/";
export const bankingUrl = "http://localhost:8080/api/v1/";
```

### Tailwind Configuration
```typescript
// tailwind.config.ts
export default {
  content: ["./app/**/*.{js,jsx,ts,tsx}"],
  theme: {
    extend: {
      fontFamily: {
        sans: ["Inter", "ui-sans-serif", "system-ui"],
      },
    },
  },
  plugins: [],
};
```

## 🎨 UI Components

### Reusable Components

#### DefaultField Component
```typescript
interface DefaultFieldProps {
  name: string;
  labelName: string;
  type: string;
  required: boolean;
  placeholder: string;
  errorMessage?: string;
  // ... other props
}
```

#### ButtonField Component
```typescript
interface ButtonFieldProps {
  type: 'submit' | 'button';
  text: string;
  disabled?: boolean;
  icon: boolean;
  // ... styling props
}
```

### Form Management
```typescript
// Example form with Formik and Yup
<Formik
  initialValues={{ email: '', password: '' }}
  validationSchema={loginSchema}
  onSubmit={handleLogin}
>
  {({ values, errors, touched, handleChange, handleBlur, isSubmitting }) => (
    <Form>
      <FormController formData={loginFormFields} />
    </Form>
  )}
</Formik>
```

## 🔐 Authentication Flow

### Login Process
1. User enters credentials
2. Frontend validates input
3. Sends request to Client Service
4. Receives JWT token
5. Stores token in localStorage
6. Redirects to dashboard

### Protected Routes
```typescript
// Authentication context
const AuthContext = createContext<AuthContextType>({
  user: null,
  login: () => {},
  logout: () => {},
  loading: false,
  token: null,
});

// Protected route wrapper
if (!user) {
  return <Navigate to="/frontend" />;
}
```

### Token Management
```typescript
// Automatic token inclusion
axios.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

## 📱 Responsive Design

### Breakpoint System
```css
/* Mobile First Approach */
.container {
  @apply px-4 sm:px-6 lg:px-8;
}

/* Responsive Grid */
.grid {
  @apply grid-cols-1 md:grid-cols-2 lg:grid-cols-3;
}
```

### Mobile Optimizations
- Touch-friendly button sizes (min 44px)
- Optimized form layouts
- Swipe gestures for navigation
- Responsive typography scaling

## 🎯 Performance Optimizations (Recommendations)

### Code Splitting
```typescript
// Lazy loading for routes
const Dashboard = lazy(() => import('./pages/dashboard/dashboard'));
const Admin = lazy(() => import('./pages/admin/admin'));
```

### Bundle Optimization
- Tree shaking for unused code
- Asset optimization with Vite
- Gzip compression
- CDN-ready static assets

### Runtime Performance
- React.memo for expensive components
- useMemo for computed values
- useCallback for event handlers
- Virtualization for large lists

## 🔄 State Management

### Context Providers
```typescript
// Authentication context
export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(false);
  
  // ... authentication logic
  
  return (
    <AuthContext.Provider value={{ user, login, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
};
```

### Local State Management
- useState for component state
- useReducer for complex state logic
- Custom hooks for reusable logic
- Context for global state

## 🎨 Styling System

### Tailwind CSS Classes
```typescript
// Consistent design system
const buttonStyles = {
  primary: "bg-blue-600 hover:bg-blue-700 text-white",
  secondary: "bg-gray-200 hover:bg-gray-300 text-gray-800",
  danger: "bg-red-600 hover:bg-red-700 text-white",
};
```

### Component Styling
```typescript
// Conditional styling
className={`
  w-full py-3 px-4 rounded-xl font-semibold transition-all duration-200
  ${activeTab === 'deposit' 
    ? 'bg-green-600 hover:bg-green-700 text-white'
    : 'bg-red-600 hover:bg-red-700 text-white'
  }
  disabled:opacity-50 disabled:cursor-not-allowed
`}
```

## 🚨 Error Handling

### Global Error Boundary
```typescript
export function ErrorBoundary({ error }: Route.ErrorBoundaryProps) {
  let message = "Oops!";
  let details = "An unexpected error occurred.";
  
  if (isRouteErrorResponse(error)) {
    message = error.status === 404 ? "404" : "Error";
    details = error.status === 404 
      ? "The requested page could not be found."
      : error.statusText || details;
  }
  
  return (
    <main className="pt-16 p-4 container mx-auto">
      <h1>{message}</h1>
      <p>{details}</p>
    </main>
  );
}
```

### API Error Handling
```typescript
// Axios error interceptor
axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Handle unauthorized access
      logout();
      navigate('/frontend');
    }
    return Promise.reject(error);
  }
);
```

## 📊 Form Validation

### Yup Schemas
```typescript
export const registrationSchema = Yup.object({
  name: Yup.string()
    .trim()
    .min(2, "Name is too short")
    .max(100, "Name is too long")
    .required("Full name is required"),
  email: Yup.string()
    .trim()
    .email("Enter a valid email")
    .required("Email is required"),
  password: Yup.string()
    .min(8, "Minimum 8 characters")
    .matches(/[A-Z]/, "At least one uppercase letter")
    .matches(/[a-z]/, "At least one lowercase letter")
    .matches(/\d/, "At least one number")
    .matches(/[^A-Za-z0-9]/, "At least one special character")
    .required("Password is required"),
});
```

## 🔧 Build Configuration

### Vite Configuration
```typescript
// vite.config.ts
export default defineConfig({
  plugins: [tailwindcss(), reactRouter(), tsconfigPaths()],
  build: {
    outDir: 'build',
    sourcemap: true,
    rollupOptions: {
      output: {
        manualChunks: {
          vendor: ['react', 'react-dom'],
          router: ['react-router'],
        },
      },
    },
  },
});
```

### TypeScript Configuration
```json
{
  "compilerOptions": {
    "target": "ES2022",
    "module": "ES2022",
    "moduleResolution": "bundler",
    "jsx": "react-jsx",
    "strict": true,
    "baseUrl": ".",
    "paths": {
      "~/*": ["./app/*"]
    }
  }
}
```

## 🚀 Deployment

### Production Build
```bash
# Build for production
npm run build

# Files will be generated in ./build directory
```

### Docker Deployment
```dockerfile
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM node:20-alpine
WORKDIR /app
COPY --from=build /app/build ./build
COPY --from=build /app/package*.json ./
RUN npm ci --omit=dev
CMD ["npm", "run", "start"]
```

### Environment-Specific Builds
```bash
# Development build
npm run build:dev

# Staging build
npm run build:staging

# Production build
npm run build:prod
```

## 📈 Monitoring & Analytics

### Performance Monitoring
- Core Web Vitals tracking
- Bundle size monitoring
- Runtime performance metrics
- User interaction tracking

### Error Tracking
- JavaScript error reporting
- API error monitoring
- User session recording
- Performance bottleneck identification

## 🛠️ Development Guidelines

### Code Style
- Use TypeScript for type safety
- Follow React best practices
- Implement proper error boundaries
- Write accessible components

### Component Development
- Create reusable components
- Use proper prop types
- Implement loading states
- Handle error states gracefully

### Performance Best Practices
- Lazy load components
- Optimize re-renders
- Use proper key props
- Implement virtualization for large lists
