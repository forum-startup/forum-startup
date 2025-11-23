import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../utils/axios.js'

export function useCreatePost() {
    const router = useRouter()

    const form = ref({
        title: '',
        content: '',
    })

    const isLoading = ref(false)
    const error = ref(null)

    async function createPost() {
        error.value = null
        isLoading.value = true

        try {
            const payload = {
                title: form.value.title,
                content: form.value.content,
            }

            await api.post('/private/posts', payload)
            await router.push('/')

        } catch (err) {
            console.error('Failed to create post:', err)

            // Handle different error types
            if (err.response) {
                // Server responded with error status (4xx, 5xx)
                error.value = err.response.data?.message || `Error ${err.response.status}`
            } else if (err.request) {
                // No response received
                error.value = 'Network error. Please check your connection.'
            } else {
                error.value = err.message || 'An unexpected error occurred'
            }
        } finally {
            isLoading.value = false
        }
    }

    return {
        form,
        isLoading,
        error,
        createPost
    }
}