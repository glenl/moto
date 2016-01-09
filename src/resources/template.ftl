% This is the default template for the moto application
% ... you might want to create your own and use that ...

\version "2.18.2"

\header {
  title = "Title Goes Here"
  composer = "Anonymous"
  mutopiatitle = "Title for Mutopia"
  license = "Creative Commons Attribution-ShareAlike 4.0"
}

\paper {
  % add space between composer/opus markup and first staff
  markup-system-spacing #'padding = #3
  % add a little space between composer and opus
  markup-markup-spacing #'padding = #1.2
  top-margin = #8
  bottom-margin = #12
}

mbreak = {} % { \break }
global = {
  \time ${time}
  \key ${key}
}

upperVoice = \relative c {
  \voiceOne
  \bar "|."
}

lowerVoice = \relative c {
  \voiceTwo
}

\score {
  <<
    \new Staff = "Guitar" \with {
      midiInstrument = #"acoustic guitar (nylon)"
%     instrumentName = #"Nº ${number}"
      \mergeDifferentlyDottedOn
      \mergeDifferentlyHeadedOn
%      \override StringNumber #'stencil = ##f
    } <<
      \global
      \clef "treble_8"

      \context Voice = "upperVoice" \upperVoice
      \context Voice = "lowerVoice" \lowerVoice
    >>
%{
    % tabs are not completely developed
    \new TabStaff = "Guitar tabs" \with {
      restrainOpenStrings = ##t
    } <<
      \clef "moderntab"
      \global
      \context TabVoice = "upperVoice" \upperVoice
      \context TabVoice = "lowerVoice" \lowerVoice
    >>
%}
  >>
  \layout {}
  \midi {
    \context { \TabStaff \remove "Staff_performer" }
    \tempo 4 = 74
  }
}
