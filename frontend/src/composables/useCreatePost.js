import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../utils/axios.js'

export function useCreatePost() {
    const router = useRouter()

    // Form state
    const form = ref({
        title: '',
        content: '',
    })

    // UI state
    const errors = ref({})
    const serverError = ref('')
    const isLoading = ref(false)

    // Backend validation rules
    const rules = {
        title: (v) => {
            const len = v.trim().length
            if (!v.trim()) return 'Title is required'
            if (len < 16) return 'Title must be at least 16 characters'
            if (len > 64) return 'Title cannot exceed 64 characters'
            return true
        },
        content: (v) => {
            const len = v.trim().length
            if (!v.trim()) return 'Content is required'
            if (len < 32) return 'Content must be at least 32 characters'
            if (len > 8192) return 'Content cannot exceed 8192 characters'
            return true
        },
    }

    // Run validation
    function validate() {
        errors.value = {}
        let isValid = true

        Object.keys(rules).forEach((key) => {
            const result = rules[key](form.value[key])
            if (result !== true) {
                errors.value[key] = result
                isValid = false
            }
        })

        return isValid
    }

    // Submit
    async function createPost() {
        serverError.value = ''
        errors.value = {}

        if (!validate()) return

        isLoading.value = true

        try {
            const payload = {
                title: form.value.title.trim(),
                content: form.value.content.trim(),
            }

            await api.post('/private/posts', payload)
            await router.push('/')

        } catch (err) {
            console.error('Failed to create post:', err)

            if (err.response?.status === 400 && err.response?.data?.errors) {
                // Spring Boot validation errors
                errors.value = err.response.data.errors
            } else if (err.response?.data?.message) {
                serverError.value = err.response.data.message
            } else if (err.response?.status === 401) {
                serverError.value = 'You must be logged in to post'
            } else {
                serverError.value = 'Failed to create post. Please try again.'
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
        createPost,
    }
}