import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../utils/axios.js'
import {usePostValidation} from "./usePostValidation.js";

export function useCreatePost() {
    const router = useRouter()

    const post = ref({
        title: '',
        content: '',
    })
    const isLoading = ref(false)
    const error = ref(null)

    const { errors, validate, clearErrors } = usePostValidation(post)

    async function createPost() {
        clearErrors()
        error.value = null

        if (!validate()) return false

        isLoading.value = true

        try {
            await api.post('/private/posts', {
                title: post.value.title.trim(),
                content: post.value.content.trim(),
            })

            // Reset post
            post.value.title = ''
            post.value.content = ''

            await router.push('/')
        } catch (err) {
            if (err.response?.data?.errors) {
                error.value = err.response.data.errors
            } else if (err.response?.data?.message) {
                error.value = err.response.data.message
            } else {
                error.value = 'Failed to create post'
            }
        } finally {
            isLoading.value = false
        }
    }

    return {
        post,
        errors,     // client side errors when validating
        serverError: error,
        isLoading,
        createPost,
    }
}