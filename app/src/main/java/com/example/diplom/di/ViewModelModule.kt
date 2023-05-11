package com.example.diplom.di

import com.example.diplom.presentation.auth.signin.SignInViewModel
import com.example.diplom.presentation.auth.signup.SignUpViewModel
import com.example.diplom.presentation.chat.ChatViewModel
import com.example.diplom.presentation.chats.ChatsViewModel
import com.example.diplom.presentation.createpost.CreatePostViewModel
import com.example.diplom.presentation.editpersoninfo.EditPersonInfoViewModel
import com.example.diplom.presentation.home.NewsViewModel
import com.example.diplom.presentation.myprofile.MyProfileViewModel
import com.example.diplom.presentation.post.PostViewModel
import com.example.diplom.presentation.posts.PostsViewModel
import com.example.diplom.presentation.prifile.ProfileViewModel
import com.example.diplom.presentation.settings.SettingsViewModel
import com.example.diplom.presentation.splash.SplashViewModel
import com.example.diplom.presentation.subscribers.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        SignUpViewModel(
            signUpUseCase = get(),
            getCategoriesUseCase = get(),
            isTakenEmailUseCase = get()
        )
    }
    viewModel { SignInViewModel(signInUseCase =  get()) }
    viewModel { SplashViewModel(tokenService = get()) }
    viewModel {
        MyProfileViewModel(
            getMyProfileUseCase = get(),
            getProfileUseCase = get(),
            getPostsByUserIdUseCase = get(),
            setLikeUseCase = get(),
            unsetLikeUseCase = get(),
            subscribeUseCase = get(),
            unsubscribeUseCase = get()
        )
    }
    viewModel { SettingsViewModel(tokenService = get()) }
    viewModel { NewsViewModel(isMyUserIdUseCase = get())}
    viewModel { ChatsViewModel(getChatsUseCase = get()) }
    viewModel { ProfileViewModel(getMyProfileUseCase = get()) }
    viewModel {
        EditPersonInfoViewModel(
            getMyProfileUseCase = get(),
            getCategoriesUseCase = get(),
            uploadUserPhotoUseCase = get()
        )
    }
    viewModel {
        ChatViewModel(
            getChatByChatIdUseCase = get(),
            getChatByUserIdUseCase = get(),
            getMessagesUseCase = get(),
            webSocketClient = get()
        )
    }
    viewModel { PostViewModel(getCommentsUseCase = get(), getPostByIdUseCase = get(), createCommentUseCase = get())}
    viewModel { CreatePostViewModel(getCategoriesUseCase = get(), createPostUseCase = get())}
    viewModel { SearchViewModel(findUsersUseCase = get(), isMyUserIdUseCase = get())}
    viewModel {
        PostsViewModel(
            getMySubscriptionsPostUseCase = get(),
            getPostsByUserIdUseCase = get(),
            getAllPostsUseCase = get(),
            setLikeUseCase = get(),
            unsetLikeUseCase = get()
        )
    }


}