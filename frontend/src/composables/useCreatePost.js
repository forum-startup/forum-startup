import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../utils/axios.js'
import {usePostValidation} from "./usePostValidation.js";
import {toast} from "vue3-toastify";

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

            await router.push('/')

            toast.success("Post created successfully!", {
                autoClose: 3000,
                position: toast.POSITION.TOP_RIGHT,
                theme: "dark",
            })

            post.value.title = ''
            post.value.content = ''
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
        errors,
        serverError: error,
        isLoading,
        createPost,
    }
}