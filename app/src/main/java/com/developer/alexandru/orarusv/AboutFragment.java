package com.developer.alexandru.orarusv;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * Created by Alexandru on 9/13/2014. Fragment displaying the app's abouts Used by the nav drawer
 */
public class AboutFragment extends Fragment {

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_about, container, false);
    TextView textDescription = view.findViewById(R.id.about_description);
    textDescription.setText(
        Html.fromHtml(inflater.getContext().getResources().getString(R.string.about_link)));
    textDescription.setMovementMethod(LinkMovementMethod.getInstance());

    TextView textGitRepo = view.findViewById(R.id.about_git);
    textGitRepo.setText(
        Html.fromHtml(inflater.getContext().getResources().getString(R.string.about_git_rep)));
    textGitRepo.setMovementMethod(LinkMovementMethod.getInstance());
    return view;
  }
}
