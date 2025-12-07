import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../utils/axios.js'

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

export function useRegisterForm() {
    const router = useRouter()

    const form = ref({
        firstName: '',
        lastName: '',
        email: '',
        username: '',
        password: '',
    })

    // UI state
    const errors = ref({})
    const serverError = ref('')
    const isLoading = ref(false)

    // Validation rules
    const rules = {
        firstName: (v) => (v.length >= 4 && v.length <= 32) || 'First name must be 4–32 characters',
        lastName:  (v) => (v.length >= 4 && v.length <= 32) || 'Last name must be 4–32 characters',
        email:     (v) => emailRegex.test(v) || 'Please enter a valid email',
        username:  (v) => !!v.trim() || 'Username is required',
        password:  (v) => (v.length >= 6 && v.length <= 50) || 'Password must be 6–50 characters',
    }

    // Run all validations
    function validate() {
        errors.value = {}
        let isValid = true

        Object.keys(rules).forEach((key) => {
            const errorMessage = rules[key](form.value[key])
            if (errorMessage !== true) {
                errors.value[key] = errorMessage
                isValid = false
            }
        })

        return isValid
    }

    async function register() {
        if (!validate()) return

        isLoading.value = true
        serverError.value = ''

        try {
            await api.post('/public/auth/register', form.value)
            await router.push('/login')
        } catch (err) {
            if (err.response?.status === 400 && err.response?.data?.errors) {
                errors.value = err.response.data.errors
            } else if (err.response?.data?.message) {
                serverError.value = err.response.data.message
            } else {
                serverError.value = 'Registration failed. Please try again.'
            }
        } finally {
            isLoading.value = false
        }
    }

    return {
        form,
        errors,
        serverError,
        isLoading,
        register,
    }
}