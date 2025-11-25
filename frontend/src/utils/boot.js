import {fetchCurrentUser} from "./auth.js";
import {currentUser, userLoading} from "./store.js";


export async function initializeApp() {
    try {
        currentUser.value = await fetchCurrentUser()
    } catch (err) {
        console.warn('Not logged in or session expired')
        currentUser.value = null
    } finally {
        userLoading.value = false
    }
}