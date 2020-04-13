package com.dancing_koala.clairdelune.ui

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.dancing_koala.clairdelune.R
import com.dancing_koala.clairdelune.android.hide
import com.dancing_koala.clairdelune.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_picture_info.*

class PictureInfoDialogFragment : DialogFragment() {

    companion object {
        fun newInstance() = PictureInfoDialogFragment()
    }

    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setStyle(STYLE_NO_FRAME, R.style.AppTheme)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_picture_info, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.selectedLockWithPictureLiveData.observe(viewLifecycleOwner, Observer {
            with(it.picture) {
                pictureInfoSize.text = "Taille: $width x $height"

                pictureInfoCreationDate.text = "Ajoutée le: ${parseDate(createdAt)}"

                description?.let { validDescription ->
                    pictureInfoDescription.text = "Description:\n\t$validDescription"
                } ?: pictureInfoDescription.hide()

                location?.let { validLocation ->
                    if (validLocation.country != null) {
                        pictureInfoLocation.text = if (validLocation.city != null) {
                            "${validLocation.city}, ${validLocation.country}"
                        } else {
                            validLocation.country
                        }
                    } else {
                        pictureInfoLocation.hide()
                    }
                } ?: pictureInfoLocation.hide()

                underlineTextView(pictureInfoWebAction)

                pictureInfoUserName.text = "@${user.username}"

                pictureInfoUserFullName.text = user.name

                user.portfolioUrl?.let {
                    underlineTextView(pictureInfoUserPortfolio)
                } ?: pictureInfoUserPortfolio.hide()

                underlineTextView(pictureInfoUserWebAction)
            }
        })

        viewModel.viewStateLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is HomeViewModel.ViewState.ShowUserProfileScreen    -> showUserProfile(it.userProfileUrl)
                is HomeViewModel.ViewState.ShowUserPortfolioScreen  -> showUserPortfolio(it.portfolioUrl)
                is HomeViewModel.ViewState.ShowUserPictureWebScreen -> showPictureWebPage(it.pictureUrl)
            }
        })

        pictureInfoWebAction.setOnClickListener { viewModel.onPictureWebsiteLinkClick() }
        pictureInfoUserPortfolio.setOnClickListener { viewModel.onUserPortfolioLinkClick() }
        pictureInfoUserWebAction.setOnClickListener { viewModel.onUserNameClick() }
    }

    private fun underlineTextView(textView: TextView) {
        val spannable = SpannableString(textView.text)
        spannable.setSpan(UnderlineSpan(), 0, spannable.length, 0)
        textView.text = spannable
    }

    private fun showUserProfile(userProfileUrl: String) = openUrl(userProfileUrl)

    private fun showUserPortfolio(portfolioUrl: String) = openUrl(portfolioUrl)

    private fun showPictureWebPage(pictureUrl: String) = openUrl(pictureUrl)

    private fun openUrl(url: String) =
        startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) })

    private fun parseDate(date: String): String {
        val dateOnly = date.split("T").first()
        return dateOnly.split("-").reversed().joinToString("/")
    }

}