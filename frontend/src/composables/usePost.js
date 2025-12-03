import {inject, ref} from 'vue'
import api from '../utils/axios.js'
import {usePostValidation} from "./usePostValidation.js";
import {currentUser} from "../utils/store.js";
import router from "../router/router.js";

export function usePost() {
    const post = ref(null)
    const isLoading = ref(false)
    const error = ref(null)
    const toast = inject('toast')
    const { errors, validate, clearErrors } = usePostValidation(post)

    async function fetchPostById(id) {
        clearErrors()
        if (!id) return

        isLoading.value = true
        error.value = null

        try {
            const res = await api.get(`/private/posts/${id}`)
            post.value = res.data

        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to load post'
        } finally {
            isLoading.value = false
        }
    }

    async function updatePost() {
        clearErrors()

        if (!validate()) {
            return false
        }

        if (!post.value?.postId) return false

        isLoading.value = true
        error.value = null

        try {
            await api.put(`/private/posts/${post.value.postId}`, {
                title: post.value.title.trim(),
                content: post.value.content.trim(),
            })

            await router.push("/my-posts")

            toast.success("Post updated successfully!", {
                autoClose: 3000,
                position: toast.POSITION.TOP_RIGHT,
                theme: "dark",
            })

            return true
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to update post'
            return false
        } finally {
            isLoading.value = false
        }
    }

    async function likePost(postId) {
        try {
            await api.post(`/private/posts/${postId}/like`)

            toast.success("You liked a post!", {
                autoClose: 3000,
                position: toast.POSITION.TOP_RIGHT,
                theme: "dark",
            })

            return true
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to like post'
            return false
        }
    }

    async function unlikePost(postId) {
        try {
            await api.post(`/private/posts/${postId}/unlike`)

            toast.success("You unliked a post!", {
                autoClose: 3000,
                position: toast.POSITION.TOP_RIGHT,
                theme: "dark",
            })

            return true
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to unlike post'
            return false
        }
    }

    async function toggleLike(postFromList) {
        if (!currentUser.value) return

        const wasLiked = postFromList.likedByCurrentUser

        postFromList.likedByCurrentUser = !wasLiked
        postFromList.likesCount += wasLiked ? -1 : 1

        try {
            if (wasLiked) {
                await unlikePost(postFromList.postId)
            } else {
                await likePost(postFromList.postId)
            }
        } catch (err) {
            postFromList.likedByCurrentUser = wasLiked
            postFromList.likesCount += wasLiked ? 1 : -1
            console.error("Like toggle failed:", err)
        }
    }


    async function deletePostById(postId) {
        isLoading.value = true
        error.value = null

        if (!confirm("Are you sure you want to delete this post? This cannot be undone.")) return false

        try {
            await api.delete(`/private/posts/${postId}`)

            await router.push("/")

            toast.success("Post deleted successfully!", {
                autoClose: 3000,
                position: toast.POSITION.TOP_RIGHT,
                theme: "dark",
            })
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to delete post'
            toast.error("Failed to delete post." || error.value)

            return false
        } finally {
            isLoading.value = false
        }
    }

    async function adminDeletePostById(postId, showMenu) {
        isLoading.value = true
        error.value = null

        if (!confirm("Are you sure you want to delete this post? This cannot be undone.")) return false

        showMenu.value = false

        try {
            await api.delete(`/admin/posts/${postId}`)

            await router.push("/")

            toast.success("Post deleted successfully!", {
                autoClose: 3000,
                position: toast.POSITION.TOP_RIGHT,
                theme: "dark",
            })
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to delete post'
            toast.error("Failed to delete post." || error.value)

            return false
        } finally {
            isLoading.value = false
        }
    }

    const isOwnPost = (post) => {
        return currentUser.value && post.creatorId === currentUser.value.id
    }

    return {
        post,
        isLoading,
        error,
        errors,
        serverError: error,
        fetchPostById,
        updatePost,
        deletePostById,
        adminDeletePostById,
        toggleLike,
        isOwnPost
    }
}