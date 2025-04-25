package com.example.training.views.workouts_listed.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.training.R
import com.example.training.databinding.LayoutWorkoutCardBinding
import com.example.training.domain.model.WorkoutItemUI
import com.example.training.setTransitionName

class WorkoutListedAdapter (private val onClickListener: (WorkoutItemUI, FragmentNavigator.Extras, View) -> Unit):
    ListAdapter<WorkoutItemUI, WorkoutListedAdapter.ViewHolder>(callbackDiffUtil) {

    class ViewHolder(private val binding: LayoutWorkoutCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setData(item: WorkoutItemUI, onClickListener: (WorkoutItemUI, FragmentNavigator.Extras, View) -> Unit) {
            binding.run {
                item.run {
                    val textTime = "$time мин"
                    titleTv.run {
                        text = title
                        setTransitionName(this, transitionName + id)
                    }
                    descriptionTv.run {
                        text = description
                        setTransitionName(this, transitionName + id)
                    }
                    timeTv.text = textTime
                    typeTv.text = root.context.getString(typeUI.typeId)

                    setTransitionName(cardInformation, cardInformation.transitionName + id)
                }
                root.setOnClickListener {
                    val extras = FragmentNavigatorExtras(
                        titleTv to titleTv.transitionName,
                        cardInformation to cardInformation.transitionName,
                        descriptionTv to descriptionTv.transitionName
                    )
                    onClickListener(item, extras, cardInformation)
                }
            }
        }
    }

    // Создает новую view
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.layout_workout_card, viewGroup, false)

        return ViewHolder(LayoutWorkoutCardBinding.bind(view))
    }

    // Меняет контент уже готовой view на новый контент
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        holder.setData(getItem(position)){ workoutItemUI, extras, view -> onClickListener(workoutItemUI, extras, view) }
    }
}

private val callbackDiffUtil = object : DiffUtil.ItemCallback<WorkoutItemUI>() {

    override fun areItemsTheSame(oldItem: WorkoutItemUI, newItem: WorkoutItemUI) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: WorkoutItemUI, newItem: WorkoutItemUI) =
        oldItem == newItem
}