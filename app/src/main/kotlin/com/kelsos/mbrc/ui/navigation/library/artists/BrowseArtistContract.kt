package com.kelsos.mbrc.ui.navigation.library.artists

import com.kelsos.mbrc.data.library.Artist
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter
import com.raizlabs.android.dbflow.list.FlowCursorList

interface BrowseArtistView: BaseView {
  fun update(data: FlowCursorList<Artist>)
}

interface BrowseArtistPresenter : Presenter<BrowseArtistView> {
  fun load()
  fun reload()
}
